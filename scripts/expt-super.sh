#!/bin/sh
cd .. 

# 3 minute micro-experiment
java -cp out/production/STAR-Vote/ sim.pseupervisor.Pseupervisor \
	open-polls-time=60000 \
	close-polls-time=500000 \
	auth-period-min=30000 auth-period-max=60000 \
	conf=supervisor.conf
