
# https://stackoverflow.com/a/37161662
# https://stackoverflow.com/a/20977657 !!! precist!

apt-get install openssh-server --assume-yes
/etc/init.d/ssh restart


for i in {1..9999}
do
	ruby "/usr/src/app/skript.rb"
	echo "$i times"
	sleep 1
done

