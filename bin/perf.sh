
mcperf --linger=0 --timeout=3 --conn-rate=1000 --call-rate=400 --num-calls=30 --num-conns=100 --sizes=u1,16 -m set -p 11212
mcperf --linger=0 --timeout=3 --conn-rate=1000 --call-rate=400 --num-calls=30000 --num-conns=100 --sizes=u1,16 -m set -p 11212
mcperf --linger=0 --timeout=3 --conn-rate=1000 --call-rate=430 --num-calls=30000 --num-conns=100 --sizes=u1,16 -m set -p 11212