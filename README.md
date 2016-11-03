# Pokedex

Simple Pokédex, utilizando Spring MVC + AngularJS.

## Desarrollo

Para iniciar la aplicación, es necesario tener instalado NodeJS. Luego ejecutar los siguientes comandos:

    npm install

    bower install --force

Si se desea utilizar gulp para la ejecución del front-end de forma independiente, será necesario instalarlo:

    npm install -g gulp-cli

Para ejecutar la aplicación, se ejcutará el siguiente comando:

    ./mvnw

La aplicación de desplegará en la dirección [http://localhost:8080](http://localhost:8080).

Si se desea utilizar gulp para separar la ejecución del front-end, se deberá ejecutar, en otro terminal, el siguiente comando:

    gulp

La aplicación de desplegará por defecto en la dirección [http://localhost:9000](http://localhost:9000).

## Versión de producción

Para generar la versión de producción, se utiliza la siguiente instrucción:

    ./mvnw -Pprod clean package

Para ejcutarla:

    java -jar target/*.war

La aplicación de desplegará en la dirección [http://localhost:8080](http://localhost:8080).

