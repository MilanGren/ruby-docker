
require "bundler/setup"
require "interpolator"

t = Interpolator::Table.new 0.1 => 2, 0.4 => 3, 0.8 => 10, 1.0 => 12, 1.2 => 11, 1.4 => 8
# LINEAR
t.style = 1

[0, 0.5, 1.0, 1.5, 2.0].each {|x| puts( t.interpolate(x)) }    


puts "ruby script in this docker container is working OK"

sleep 20

puts "ruby script in this docker container is working OK"