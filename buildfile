# buildfile for bering

require 'buildr/bnd'

VERSION_NUMBER = "0.8.2-SNAPSHOT"
GROUP = 'edu.northwestern.bioinformatics'

repositories.release_to[:url] = "sftp://ligand/var/www/sites/download/download/maven2"

define 'bering' do
  project.version = VERSION_NUMBER
  project.group = GROUP

  compile.options.target = '1.5'
  compile.options.source = '1.5'
  compile.options.other = %w(-encoding UTF-8)

  define 'core' do
    compile.with SPRING, GROOVY, ANT, SERVLET, HIBERNATE, JAKARTA_COMMONS
    test.compile.with UNIT_TEST, HSQLDB
    test.resources
    package(:bundle,  :id => 'bering').tap do |bundle|
      bundle['Export-Package'] = [
        'ant', 'dialect', 'dialect.hibernate',
        'runtime', 'runtime.classpath', 'runtime.filesystem',
        'servlet', 'tools', nil
      ].collect { |sub| ["edu.northwestern.bioinformatics.bering", sub].compact.join('.') }.
        join(',')
      bundle['Import-Package'] = '*;resolution:=optional'
      bundle['Bundle-Name'] = 'Bering'
      bundle['Bundle-Description'] = 'Bering, a database refactoring and migration system.'
      bundle['DynamicImport-Package'] = '*' # for JDBC drivers
    end
    package(:javadoc, :id => 'bering')
    package(:sources, :id => 'bering')
  end

  define 'maven-plugin' do
    compile.with project('core').and_dependencies, MAVEN, HSQLDB
    test.compile.with UNIT_TEST
    package(:jar)
    package(:javadoc)
    package(:sources)
  end
end
