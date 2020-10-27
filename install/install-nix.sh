#!/bin/bash


# DEST_DIR: where to install Gralog
# DO_INSTALL_PYTHON: default is 1
# PYTHON_LIB_DIR: where to install Gralog.py
# PYTHON_TO_SYSTEM: deafult 1 (install python packages to system directories)
#                           0 (install python packages to user directories)

# If no arguments: DEST_DIR is $HOME/.gralog, DO_INSTALL_PYTHON is 1, PYTHON_TO_SYSTEM is 1.
# One argument should be -u (install python packages to user directories)
# or -n (don't install the python parth of Gralog).

# E.g.
# ./install-linux.sh $HOME/my/path/gralog -u
# means that Gralog will be installed to $HOME/my/path/gralog and the python packages to user directories.
#
# ./install-linux.sh  -u
# means that Gralog will be installed to $HOME/gralog (default) and the python packages
# to user directories.
#
# ./install-linux.sh  -n
# means that Gralog will be installed to $HOME/gralog (default) and no the python packages
# will be installed.
#
# ./install-linux.sh  $HOME/my/path/gralog 
# means that Gralog will be installed to $HOME/my/path/gralog  and the python packages
# to system directories (default).



source fncts.sh


read_arguments "$@"

# Creating and setting DEST_DIR if necessary

echo DEST_DIR: $DEST_DIR

if  [ -d "$DEST_DIR" ]; then
    echo Installing Gralog to "$DEST_DIR"
    if [ -w "$DEST_DIR" ]; then
	DEST_DIR_WRITABLE=1
    else
	DEST_DIR_WRITABLE=0
    fi
else
    if [ -f "$DEST_DIR" ]; then
	echo "Deleting file (which is not a directory) $DEST_DIR"
	if [ "$(rm $DEST_DIR)" > 0 ]; then # try to delete file (not directory) DEST_DIR; if didnt work
	    sudo rm "$DEST_DIR"
	fi
    fi
    echo Directory $DEST_DIR does not exist, creating it.
    if [ "$(mkdir -p $DEST_DIR)" > 0 ]; then # try to create DEST_DIR; if didnt work
       sudo mkdir -p "$DEST_DIR"
       DEST_DIR_WRITABLE=0
    else
       DEST_DIR_WRITABLE=1
    fi
fi



install_gralog

if [ $DO_INSTALL_PYTHON ]; then
    install_python_part        # python-igraph, networkx and Gralog.py
fi


echo "Installation complete."

