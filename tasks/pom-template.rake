require 'buildr'

# A small buildr extension that takes a POM template and creates a POM
# from it.

module PomTemplate
  include Buildr::Extension

  after_define do |project|
    tmpl = project._('pom.template.xml')
    pom = project._(:target, 'pom.xml')
    if File.exist? tmpl
      task pom => tmpl do
        project.filter(File.dirname(tmpl)).
          include(File.basename(tmpl)).
          into(File.dirname(pom)).
          using(:ant, 'VERSION_NUMBER' => project.version).
          run
        mv File.join(File.dirname(pom), File.basename(tmpl)), pom
      end
      project.package(:jar).enhance [pom]
      project.package(:jar).pom.from pom
    end
  end
end

class Buildr::Project
  include PomTemplate
end
