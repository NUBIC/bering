# buildfile for bering

VERSION_NUMBER = '0.8.0'
GROUP = 'edu.northwestern.bioinformatics'

define 'bering' do
  project.version = VERSION_NUMBER
  project.group = GROUP

  compile.options.target = '1.5'
  compile.options.source = '1.5'
  compile.options.other = %w(-encoding UTF-8)

  package_with_sources
  package_with_javadoc

  define 'core' do
    compile.with SPRING, GROOVY, ANT, SERVLET, HIBERNATE, JAKARTA_COMMONS
    test.compile.with UNIT_TEST, HSQLDB
    test.resources
    package(:jar)
  end

  define 'maven-plugin' do
    compile.with project('core').and_dependencies, MAVEN, HSQLDB
    test.compile.with UNIT_TEST
    package(:jar)
  end
end
