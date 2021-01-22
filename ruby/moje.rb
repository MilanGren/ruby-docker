
require "bundler/setup"
require "interpolator"

@outputPath = "myvol/output"

t = Interpolator::Table.new 0.1 => 2, 0.4 => 3, 0.8 => 10, 1.0 => 12, 1.2 => 11, 1.4 => 8

data = ""
if(File.exist?(@outputPath))
  puts "#{@outputPath} exits"
  data << IO::read(@outputPath) << "\n\n"
else
  puts "#{@outputPath} not exists"
  data << "ruby script in this docker container is working OK\n\n"
end

[0, 0.5, 1.0, 1.5, 2.0].each {|x| data <<  t.interpolate(x).to_s << "\n" }    

IO::write(@outputPath,data)

puts "--"
puts "ruby script in this docker container seems working OK "
puts "--"

# sleep 999