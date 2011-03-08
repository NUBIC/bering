repositories.remote << 'http://www.ibiblio.org/maven2'

###
# Helpers
module Deps
  # Versions for libraries with more than one artifact
  VERSIONS = Buildr.struct(
    :spring => '2.0.3',
    :maven  => '2.0.5'
  )

  def self.spring(name)
    "org.springframework:#{name}:jar:#{VERSIONS.spring}"
  end
end

###

# TODO: split this up
SPRING = struct(
  :all => Deps.spring('spring')
)

GROOVY = struct(
  :main  => 'groovy:groovy:jar:1.0-jsr-06',
  :cglib => 'cglib:cglib-nodep:jar:2.1_3',
  :asm   => 'asm:asm:jar:2.2',
  :antlr => 'antlr:antlr:jar:2.7.5'
)

HIBERNATE = struct(
  :main => 'org.hibernate:hibernate:jar:3.2.1.ga'
)

ANT = 'ant:ant:jar:1.6.5'

SERVLET = 'javax.servlet:servlet-api:jar:2.3'

JAKARTA_COMMONS = struct(
  :logging => 'commons-logging:commons-logging:jar:1.1', # TODO: use slf4j
  :lang    => 'commons-lang:commons-lang:jar:2.1',
  :io      => 'commons-io:commons-io:jar:1.3.1'
)

UNIT_TEST = struct(
  :easymock     => 'org.easymock:easymock:jar:2.2',
  :easymock_clz => 'org.easymock:easymockclassextension:jar:2.2.1',
  :spring_mock  => Deps.spring('spring-mock')
)

HSQLDB = 'hsqldb:hsqldb:jar:1.8.0.7'

MAVEN = struct(
  :project    => "org.apache.maven:maven-project:jar:#{Deps::VERSIONS.maven}",
  :plugin_api => "org.apache.maven:maven-plugin-api:jar:#{Deps::VERSIONS.maven}",
  :model      => "org.apache.maven:maven-model:jar:#{Deps::VERSIONS.maven}"
)
