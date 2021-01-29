
# https://stackoverflow.com/a/37161662
# https://stackoverflow.com/a/20977657 !!! precist!

apt-get install openssh-server --assume-yes

KEY=id_rsa_milan

mkdir -p /root/.ssh
cp $KEY.pub /root/.ssh/$KEY.pub
cat $KEY.pub >> /root/.ssh/authorized_keys
chmod 0600 /root/.ssh/authorized_keys

/etc/init.d/ssh restart

for i in {1..9999}
do
	ruby "/usr/src/app/skript.rb"
	echo "$i times"
	sleep 1
done

