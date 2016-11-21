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
                return filePath.replace(/^clientside\/js\/templates\//, '').replace(/\.hbs$/, '');
            }

        },
        all: {
            files: {
                "public/js/templates.js": ["clientside/js/templates/*.hbs"]
            }
        }
    };


    config.less = {
        development: {
            options: {
                paths: ["clientside/style"]
            },
            files: {
                "public/css/main.css": "clientside/style/main.less"
            }
        }
    };

    config.less.production = config.less.development;

    config.concat = {
        development: {
            files: {
                "public/js/views.js": ["clientside/js/views/*.js"],
                "public/js/models.js": ["clientside/js/models/*.js"],
                "public/js/app.js": ["clientside/js/app/app.js"],
                "public/js/view-features.js": ["clientside/js/viewfeatures/*.js"]
            }
        }
    };

    config.concat.production = config.concat.development;

    config.watch = {
       handlebars: {
           files: ["clientside/js/templates/*.hbs"],
           tasks: ["handlebars"]
       },
       less: {
            files: ["clientside/style/*.less"],
            tasks: ["less"]
       },
       concat: {
            files: ["clientside/js/views/*.js", "clientside/js/app/app.js", "clientside/js/models/*.js"],
            tasks: ["concat"]
       }
    };

    grunt.initConfig(config);

    grunt.loadNpmTasks("grunt-bower-task");
    grunt.loadNpmTasks("grunt-contrib-handlebars");
    grunt.loadNpmTasks("grunt-contrib-less");
    grunt.loadNpmTasks("grunt-contrib-watch");
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.registerTask("default", ["bower", "handlebars", "less", "concat"]);

};