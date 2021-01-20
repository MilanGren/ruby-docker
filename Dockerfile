
#FROM ubuntu
# RUN apt-get update && apt-get install -y joe tree

FROM ruby:2.6.6
RUN apt-get update && apt-get install -y joe tree

# set a directory for the app
WORKDIR /usr/src/app

# copy all the files to the container
COPY . .

#RUN apt-get update && apt-get install -y joe 

# install dependencies
#RUN pip install --no-cache-dir -r requirements.txt

# tell the port number the container should expose
#EXPOSE 5000

# run the command
entrypoint ["ruby", "/usr/src/app/moje.rb"]





#CMD ruby -e "puts 1 + 2"

#entrypoint ["echo", "milan"]

#RUN ruby /usr/src/app/moje.rb
