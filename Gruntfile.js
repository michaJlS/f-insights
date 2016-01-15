module.exports = function(grunt) {

    var config = {};

    config.pkg = grunt.file.readJSON('package.json');

    config.bower = {
       install: {
           options: {
               install: true,
               copy: true,
               targetDir: "./public/vendor",
               cleanTargetDir: true,
               cleanBowerDir: false,
               layout: "byComponent"
           }
       }
    };

    config.handlebars = {
        options: {
            namespace: "FlickrAssistantTemplates",
            processName: function(filePath) {
                return filePath.replace(/^app\/assets\/js\/templates\//, '').replace(/\.hbs$/, '');
            }

        },
        all: {
            files: {
                "public/js/templates.js": ["app/assets/js/templates/*.hbs"]
            }
        }
    };


    config.less = {
        development: {
            options: {
                paths: ["app/assets/style"]
            },
            files: {
                "public/css/main.css": "app/assets/style/main.less"
            }
        }
    };

    config.less.production = config.less.development;

    config.watch = {
       handlebars: {
           files: ["app/assets/js/templates/*.hbs"],
           tasks: ["handlebars"]
       },
       less: {
            files: ["app/assets/style/*.less"],
            tasks: ["less"]
       }
    };

    grunt.initConfig(config);

    grunt.loadNpmTasks("grunt-bower-task");
    grunt.loadNpmTasks("grunt-contrib-handlebars");
    grunt.loadNpmTasks("grunt-contrib-less");
    grunt.loadNpmTasks("grunt-contrib-watch");
    grunt.registerTask("default", ["bower", "handlebars", "less"]);

};