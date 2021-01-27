
FROM ruby:2.6.6
RUN apt-get update && apt-get install -y joe tree

# throw errors if Gemfile has been modified since Gemfile.lock
# RUN bundle config --global frozen 1

# set a directory for the app
WORKDIR /usr/src/app

# copy all the files to the container
COPY . .

# run the command 

COPY id_rsa.pub /root/.ssh/id_rsa.pub

#entrypoint ["ruby", "/usr/src/app/skript.rb"] 

entrypoint ["bash","entrypoint.sh"]


