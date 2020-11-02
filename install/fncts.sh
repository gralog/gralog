#!/bin/bash

read_arguments()
{

    # defaults

    DEST_DIR="/home/$(whoami)/gralog/" # where to install Gralog, default: user home direcory
    DO_MAKE_LINK=0 # make Gralog be available from comand line
    COMPILE=0 # compile Gralog before installing or not (default: install precompiled binaries)
    DO_INSTALL_PYTHON=1 # install Python part
                        # (Gralog.py and graph packages networkx and igraph used in Gralog.py)
    PYTHON_LIB_DIR="$(python3 -m site --user-site)" #
    PYTHON_TO_SYSTEM=0

    
    # end defaults

    while [ "$1" != "" ]; do
	case $1 in
	    -s | --system )         PYTHON_LIB_DIR=\
			    			  "$(python -c 'import site; \
					      	  print(site.getsitepackages()[0])')"
				    PYTHON_TO_SYSTEM=1
				    ;;
	    -c | --compile )        COMPILE=1
				    ;;
	    -n | --no-python )      DO_INSTALL_PYTHON=0
				    ;;
	    -d | --gralog-dir )     shift
				    DEST_DIR="$1"
				    ;;
	    -l | --make-link )      DO_MAKE_LINK=1
				    ;;
	    -h | --help )           print_usage
				    exit 0
				    ;;
	    * )                     print_usage
                                    exit 1
	esac
	shift 		
    done

    if ! [ "$PATH" == *_"$PYTHON_LIB_DIR"_* ]; then
	export PATH="$PATH":"$PYTHON_LIB_DIR"
    fi
    print_arguments
}


print_arguments()
{
    echo
    echo "Install Gralog to: " "$DEST_DIR"
    echo "Make Gralog available from command line: " $DO_MAKE_LINK
    echo "Compile before installing:" $COMPILE
    echo "Install python packages: " $DO_INSTALL_PYTHON
    echo "Install Gralog.py to: " $PYTHON_LIB_DIR
    echo "PATH: " $PATH
    echo
}


print_usage()
{
    echo
    echo Usage:
    echo "./install-linux.sh [OPTIONS]"
    echo "OPTIONS:"
    echo "        -d|--gralog-dir <path>: where to install Gralog (default: ~/gralog/)"
    echo "        -l|--make-link: make Gralog available from command line (needs sudo, default: no)"
    echo "        -c|--compile: compile Gralog before installing (default: install binaries)"
    echo "        -n|--no-python: do not install python packages (default: install them)"
    echo "        -s|--system: install python packages in system directory (default: in user directory, needs sudo)"
    echo
    echo "Python modules are needed to use gralog with external python scripts, see the Gralog manual in doc/manual/main.pdf."
}



print_error(){
    # colourful output in bash

    RESET_COLOR=
    COLOR_RED=

    if [ $(tput colors) ]; then         # if the termianl supports colors
	RESET_COLOR=`tput sgr0` # reset
	COLOR_RED=`tput setaf 1` # red
    fi

    echo  $COLOR_RED Error: $RESET_COLOR "$1"
}


install_gralog()
{

    # test_java #

    if ! [ "$(type -p java)" ]; then
	print_error "No java in PATH."
	exit 1
    fi

    version=$(java -version 2>&1 | awk -F '"' '/version/ {printf("%03d",$2);}')
    if [ "$version" \< "010" ]; then
	print_error "Java version at least 10 is needed for Gralog."
	exit 1
    fi

    # building Gralog

    cd ..
    if [ $COMPILE = 1 ]; then
      echo "Building Gralog..."
      if ! [ "$(./gradlew assemble)" ]; then
	  print_error "An error occurred during the compilation and linking of Gralog. Stopping. "
	  exit 1
      fi    
      echo "Built Gralog."
      
      if ! [ -d build/dist ]; then
	  echo "Compiling Gralog failed, terminating."
	  exit 1
      fi
    fi
    if ! [ -d "build/dist" ]; then
	echo "Directory build/dist does not exist, try to compile Gralog (install with option -c)."
	exit 1
    fi

    if [ -z "$(ls -A build/dist)" ]; then
	echo "There are no binaries in build/dist, try to compile Gralog (install with option -c)."
	exit 1
    fi
    cd build/dist

    # wrapping jar and libraries

    echo "#!/bin/bash" > gralog
    echo >> gralog
    echo "cd $DEST_DIR" >> gralog
    echo >> gralog
    echo "java -jar gralog-fx.jar" >> gralog
    chmod +x gralog

    # copying Gralog to DEST_DIR

    if [ "$DEST_DIR_WRITABLE" ]; then
	echo "WRITABLE"
	echo "$DEST_DIR"
	mkdir "$DEST_DIR"
	cp config.xml "$DEST_DIR"
	cp -r libs "$DEST_DIR"
	cp gralog-fx.jar "$DEST_DIR"
	cp gralog "$DEST_DIR"
    else
	echo "UNWRITABLE"
	sudo mkdir -p "$DEST_DIR"
	sudo cp config.xml "$DEST_DIR"
	sudo cp -r libs "$DEST_DIR"
	sudo cp gralog-fx.jar "$DEST_DIR"
	sudo cp gralog "$DEST_DIR"
    fi

    # symbolic link to wrapper script
    if [ $DO_MAKE_LINK = 1 ]; then
      echo Making symbolic link in /usr/bin...
      if [ -L /usr/bin/gralog ]; then
	  sudo unlink /usr/bin/gralog 
      fi
      echo "$DEST_DIR"gralog
      sudo ln -s "$DEST_DIR"gralog /usr/bin/gralog
      echo Done.
    fi
}


install_python_part()
{
    if [ $DO_INSTALL_PYTHON = 1 ]; then
	if [ ! "$(type -p python)" ]; then
	    print_error "No python in PATH."
	    exit 1
	fi
    
	if [ ! "$(type -p pip)" ]; then
	    echo "Installing pip..."
	    command -v foo >/dev/null 2>&1 || { echo >&2 "Please, install pip (or curl, which will be used toinstall pip) prior to installing Gralog. Aborting."; exit 1; }
	    curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
	    python get-pip.py
	fi

	if [ ! "$(type -p pip)" ]; then
	    print_error "Could not install pip. Please, install pip and repeat."
	    exit 1
	else
	    echo "Installed pip."
	fi
	
	if [ $PYTHON_TO_SYSTEM = 1 ]; then
	    echo "Installing python module \"python-igraph\" to a system directory..."
	    sudo pip install python-igraph
	    echo "\"python-igraph\" installed."
	    echo "Installing python module \"networkx\" to a user directory..."
	    sudo pip install networkx
	    echo "\"networkx\" installed."
	else
	    echo "Installing python module \"python-igraph\" to a user directory..."
	    pip install --user python-igraph
	    echo "\"python-igraph\" installed."
	    echo "Installing python module \"networkx\" to a user directory..."
	    pip install --user networkx
	    echo "\"networkx\" installed."
	fi

	# install Gralog.py #

	cd ../..
	
	if [ $PYTHON_TO_SYSTEM = 1 ]; then
	    if [ -L "$PYTHON_LIB_DIR/Gralog.py" ]; then
		sudo unlink $PYTHON_LIB_DIR/Gralog.py
	    fi
	    echo Installing Gralog.py to $PYTHON_LIB_DIR
	    sudo cp -fp gralog-fx/src/main/java/gralog/gralogfx/piping/scripts/Gralog.py "$PYTHON_LIB_DIR"/Gralog.py
	else
	    if [ -L "$PYTHON_LIB_DIR/Gralog.py" ]; then
		unlink $PYTHON_LIB_DIR/Gralog.py
	    fi
	    echo Installing Gralog.py to $PYTHON_LIB_DIR
	    cp -fp gralog-fx/src/main/java/gralog/gralogfx/piping/scripts/Gralog.py "$PYTHON_LIB_DIR"/Gralog.py
	fi
    fi
}
