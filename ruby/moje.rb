
#require "bundler/setup"
#require "interpolator"

#t = Interpolator::Table.new 0.1 => 2, 0.4 => 3, 0.8 => 10, 1.0 => 12, 1.2 => 11, 1.4 => 8

# LINEAR
#t.style = 1


data = "ruby script in this docker container is working OK\n\n"

#[0, 0.5, 1.0, 1.5, 2.0].each {|x| data <<  t.interpolate(x) }    


IO::write("/myvol/output",data)

puts "--"
puts "ruby script in this docker container seems working OK "
puts "--"

sleep 1