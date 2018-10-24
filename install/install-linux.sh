#!/bin/bash

DEST_DIR='~/gralog/' # where to install Gralog
PYTHON_LIB_DIR='~/python' # where to install python modules


# colourful output in bash

RESET_COLOR=
COLOR_RED=

if [ test_colored_terminal ]; then
   RESET_COLOR='\e[0m' # reset
   COLOR_RED='\e[0;31m' # red
fi

# test_java #

if [ type -p java ]; then
else
    print_error("No java in PATH.")
    exit()
fi

version=$(java -version 2>&1 | awk -F '"' '/version/ {print("%03d",$2);}')
if [ "$version" < "010" ]; then
    print_error("Java version at least 10 is needed for Gralog.")
    exit()
fi

# building Gralog

echo "Building Gralog..."
cd ..
./gradlew build
cd build/dist
echo "Built Gralog."

# wrapping jar and libraries

echo "#!/bin/bash\n\njava -jar gralog-fx.jar" > gralog
chmod +x gralog

# copying Gralog to DEST_DIR

if [ w_rights in $DEST_DIR ]; then
    cp -r config.xml libs gralog-fx.jar gralog DEST_DIR
else
    sudo cp -r config.xml libs gralog-fx.jar gralog DEST_DIR
fi

# symbolic link to wrapper script

sudo ln -s /usr/bin/gralog DEST_DIR/gralog

# installing python-igraph, networkx and Gralog.py

if [ do_install_python ]; then
    install_python_part()
fi


echo "Installation complete"

print_error(text){
    echo "$COLOR_RED"Error:"$RESET_COLOR" $text
}


install_python_part()
{
    if [ !type -p python ]; then
	print_error("No python in PATH.")
	exit()
    fi

    if [ !type -p pip ]; then
	echo "Installing pip..."
	curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
	python get-pip.py
    fi

    if [ !type -p pip ]; then
	print_error("Could not install pip. Please, install pip and repeat.")
	exit()
    else
	echo "Installed pip."
    fi
    
    if [ install_py_libs_with_sudo ]; then
	echo "Installing python module \"python-igraph\"..."
	sudo pip install python-igraph
	echo "Installed \"python-igraph\"."
	echo "Installing python module \"networkx\"..."
	sudo pip install networkx
	echo "Installed \"networkx\"."

    else
	echo "Installing python module \"python-igraph\"..."
	pip install --user python-igraph
	echo "Installed \"python-igraph\"."
	echo "Installing python module \"networkx\"..."
	pip install --user networkx
	echo "Installed \"networkx\"."
    fi

    # install Gralog.py #

    cd ../..
    
    if [ w_rights in $PYTHON_LIB_DIR ]; then
	ln -s gralog-fx/src/main/java/gralog/gralogfx/piping/scripts/Gralog.py $PYTHON_LIB_DIR/Gralog.py
    else
	sudo ln -s gralog-fx/src/main/java/gralog/gralogfx/piping/scripts/Gralog.py $PYTHON_LIB_DIR/Gralog.py
    fi
}
