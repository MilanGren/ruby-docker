
i:
	docker build -t rd .
	
r:
#	docker run -itd --name rdcon rd
	docker run rd



# docker stop $id
# docker rm $id

# pass id by name to variable 'a' and print it
# a=$(docker ps -aqf "name=rdcon") ; echo $a
