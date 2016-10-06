#!/bin/sh

cd /home/mdb12/Workspace/STAR-Vote
scriptdir=./scripts
startdir=`pwd`



echo "starting supervisor"
sh $scriptdir/e2-super.sh &
disown %

sleep 1

for x in 1 2 3 4 5 6 7 8; do
	echo "starting $x"
	ssh -fn sysrack0$x "$startdir/$scriptdir/e2-booth.sh"
	sleep 1
done
