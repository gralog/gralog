#!/bin/bash

read_arguments()
{

    if [ "$#" -ge 3 ]; then
	echo Too many parameters.
	print_usage
	exit 1
    fi


    DO_INSTALL_PYTHON=1
    DEST_DIR="$HOME/gralog/" # where to install Gralog
  
    if [ $# == 0 ]; then
	# DEST_DIR remains default, install python packages to the system directory (default)
	PYTHON_LIB_DIR="$(python -c 'import site; print(site.getsitepackages()[0])')"    
	PYTHON_TO_SYSTEM=1
    elif [ $# == 1 ]; then
	parameter=$1
	if [ ${parameter:0:2} == '-u' ]; then
	    # DEST_DIR remains default, install python packages to the user directory
	    PYTHON_LIB_DIR="$(python -m site --user-site)"
	    PYTHON_TO_SYSTEM=0
	elif [ ${parameter:0:2} == '-n' ]; then
	    # DEST_DIR remains default, do not install python packages
	    DO_INSTALL_PYTHON=0
	elif [ ${parameter:0:1} == '-' ]; then
	    echo "Wrong parameters."
	    print_usage
	    exit 1
	else
	    # The parameter is DEST_DIR, install python packages to the system directory (default)
	    PYTHON_LIB_DIR="$(python -c 'import site; print(site.getsitepackages()[0])')"    
	    PYTHON_TO_SYSTEM=1
	    DEST_DIR="$1"gralog
	fi
    else
	# two parameters
	first="$1"
	second="$2"
	if [ ${first:0:1} == "-" ]; then
	    # should be -u or -n
	    # DEST_DIR is the second parameter
	    DEST_DIR="$2"gralog
	    if [ ${first:0:2} == "-u" ]; then
		# install python packages to the user directory
		PYTHON_LIB_DIR="$(python -m site --user-site)"
		PYTHON_TO_SYSTEM=0
	    elif [ ${first:0:2} == "-n" ]; then
		DO_INSTALL_PYTHON=0
	    else
		echo "Wrong parameters."
		print_usage
		exit 1
	    fi
	else
	    # The first parameter is DEST_DIR
	    DEST_DIR="$1"gralog
	    # The second should be "-u" or "-n"
	    if [ ${second:0:2} == "-u" ]; then
		# install python packages to the user directory
		PYTHON_LIB_DIR="$(python -m site --user-site)"
		PYTHON_TO_SYSTEM=0
	    elif [ ${second:0:2} == "-n" ]; then
		DO_INSTALL_PYTHON=0
	    else
		echo "Wrong parameters."
		print_usage
		exit 1
	    fi
	fi
    fi

    echo Destination directory: $DEST_DIR

}




print_usage()
{
    echo Usage:
    echo "./install-linux.sh [<path>][ -u| -n]"
    echo "        <path>: where to install Gralog"
    echo "        -u: install python packages in user directory"
    echo "        -n: install python packages in system directory"
    echo
    echo "Python modules are needed to use gralog with external python scripts, see the Gralog manual."
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

    echo "Building Gralog..."
    cd ..
    if ! [ "$(./gradlew build)" ]; then
	print_error "An error occurred during the compilation and linking of Gralog. Stopping. "
	exit 1
    fi    
    echo "Built Gralog."
    
    if ! [ -d build/dist ]; then
	echo "Compiling Gralog failed, terminating."
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
	cp config.xml "$DEST_DIR"
	cp -r libs "$DEST_DIR"
	cp gralog-fx.jar "$DEST_DIR"
	cp gralog "$DEST_DIR"
    else
	sudo cp config.xml "$DEST_DIR"
	sudo cp -r libs "$DEST_DIR"
	sudo cp gralog-fx.jar "$DEST_DIR"
	sudo cp gralog "$DEST_DIR"
    fi

    # symbolic link to wrapper script
    echo Making symbolic link in /usr/bin...
    if [ -L /usr/bin/gralog ]; then
	sudo unlink /usr/bin/gralog 
    fi
    echo "$DEST_DIR"gralog
    sudo ln -s "$DEST_DIR"/gralog /usr/bin/gralog
    echo Done.
}


install_python_part()
{
    if [ ! "$(type -p python)" ]; then
	print_error "No python in PATH."
	exit 1
    fi
    



    if [ ! "$(type -p pip)" ]; then
	echo "Installing pip..."
	curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
	python get-pip.py
    fi

    if [ ! "$(type -p pip)" ]; then
	print_error "Could not install pip. Please, install pip and repeat."
	exit 1
    else
	echo "Installed pip."
    fi
    
    if [ "$PYTHON_TO_SYSTEM" ]; then
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
    
    if [ "$PYTHON_TO_SYSTEM" ]; then
	if [ -L "$PYTHON_LIB_DIR/Gralog.py" ]; then
	    sudo unlink $PYTHON_LIB_DIR/Gralog.py
	fi
	echo Installing Gralog.py to $PYTHON_LIB_DIR
	sudo cp -f gralog-fx/src/main/java/gralog/gralogfx/piping/scripts/Gralog.py "$PYTHON_LIB_DIR"/Gralog.py
    else
	if [ -L "$PYTHON_LIB_DIR/Gralog.py" ]; then
	    unlink $PYTHON_LIB_DIR/Gralog.py
	fi
	echo Installing Gralog.py to $PYTHON_LIB_DIR
	cp -f gralog-fx/src/main/java/gralog/gralogfx/piping/scripts/Gralog.py "$PYTHON_LIB_DIR"/Gralog.py
    fi
}
