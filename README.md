
## Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW

HECHO POR: Jeisson Steban Casallas Rozo y David Leonardo Piñeros Cortés

#### Ejercicio – programación concurrente, condiciones de carrera y sincronización de hilos. EJERCICIO INDIVIDUAL O EN PAREJAS.

##### Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?

![](img/grafico.png)

**Respuesta:** Consume mas CPU en la clase consumer debido a que esta haciendo una verificación if permanentemente, donde le pregunta si el tamaño de la lista es mayor a cero. Este consumo actualmente esta variando entre el 5% al 8% de uso.

2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.

![](img/graficoOptimizado.png)
![](img/parte2Antes.png)
![](img/parte2AntesPro.png)
![](img/parte2main.png)

**Respuesta:** Ahora el consumo de la CPU, se disminuyo a menor del 1%, con ayuda de que ahora quitamos la verificación del if y ahora manejamos el ingreso a la cola de los dos hilos con un wait y un notify. De manera que cuando la cola este vacia, el hilo de consumer tenga que esperar y que cuando el hilo producer agregue a la cola este notifique al consumer para activarlo nuevamente.

3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise el API de la colección usada como cola para ver cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya consumo alto de CPU ni errores.

![](img/consumer.png)
![](img/producer.png)
![](img/starting.png)

**Respuesta:** Guiados por la pista que se encontraba en el main de usar una LinkedBlockingQueue, decidimos cambiar toda la implementación hecha en el punto anterior y enfocarla en base el uso de ColasEnlazadas, las cuales en sus metodos para manejarlas ellas mismas controlan la concurrencia del programa, por ende el productor y el consumidor no entran al mismo tiempo a la cola. Ademas de eso implementamos un limite para la cola, este consiste en que no pueda tener mas de 10 elementos, de esta forma controlamos el flujo con el que ingresan y salen datos, ademas de un sleep, para disminuir la intensidad del consumer al eleminiar un valor de la cola.

##### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).
- Lo anterior, garantizando que no se den condiciones de carrera.

![](img/variableatomica.png)
![](img/Blacklist.png)

**Respuesta:** Con la ayuda de una variable atomica, en este caso un contador global que fuera sumando la ocurrencias encontradas por los hilos de 1 en 1, logramos que estos se comunicaran entre si, sin ocasionar posible condiciones carrera debido a que la misma variable maneja la ocurrencia del programa. 
De esta forma se optimiza para que no tenga que recorrer las 80 mil listas, si no termine cuando encuentra 5 coincidencias con la ip.

##### Parte III. – Avance para el martes, antes de clase.

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:

	* Se tienen N jugadores inmortales.
	* Cada jugador conoce a los N-1 jugador restantes.
	* Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.
	* El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.

2. Revise el código e identifique cómo se implemento la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de vida). Para este caso, para N jugadores, cual debería ser este valor?.

**Respuesta:** Debido todos los jugadores iniciamente tienen definido 100 de vida, el valor de la sumatoria deberia ser la multiplicación de los N jugadores por la vida que tienen estos, de esta forma obtendriamos el invariante. 

 Invariante = N * 100

3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. Se cumple el invariante?.

**Respuesta:** En este caso no se mantiene el invariante de la vida, pues cuando damos pause and check este realiza la suma de los de los puntos de cada jugador obteniedo el total, sin embargo, se está dando una condición de carrera al calcular este valor.

4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.

![](img/immortal1.PNG)
![](img/immortal2.PNG)
![](img/immortal3.PNG)

**Respuesta:** Con esta nueva implementación logramos parar los hilos al momento de presionar el boton "Pause And check" de forma que estos se encuentren detenidos antes de realizar la suma y tambien el botón "Resume" para notificarles a los hilos que pueden seguir con su ejecución.

5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). Se cumple o no el invariante?.

![](img/PruebaJuego.png)
![](img/immortal4.PNG)

**Respuesta:** No se cumple el invariante ya que aún siguen presentes las condiciones
de carrera.

6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:

	```java
	synchronized(locka){
		synchronized(lockb){
			…
		}
	}
	```

![](img/immortal5.PNG)

**Respuesta:** En este caso guiados por el laboratorio, implementamos la estrategia de los locks anidados en el metodo fight.

7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.

	![](img/immortal6.png)
	![](img/immortal7.png)

**Respuesta:**  Debido a que ahora ocasionamos un deadlock, el programa se queda detenido, verificando con ayuda de jstack podemos ver que el deadlock se ocasiono entre los 3 inmortales que tenemos en la ejecución, debido estan en espera entre sí a que liberen el bloqueo.

8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).

![](img/immortal8.PNG)
![](img/immortal9.jpg)

**Respuesta:** Para este caso seguimos una de las estrategias para evitar un deadlock, que es darle un orden a su ejecución, en este caso seleccionamos la ejecución comparando el valor Hash entre los dos inmortales, adicionalmente fue necesario modificar el metodo fight, por lo cuál deben llegar como parametros los 2 inmortales que van a luchar y no solo uno como se tenia inicialmente. Con esto tambien solucionamos una condición carrera con respecto a que la sumatoria de la vida no era siempre N*100, por lo que ahora si se cumple este invariante.

9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.

![](img/inmortal10.png)
![](img/inmortal11.png)

**Respuesta:**  Realizamos las ejecuciones para 100 inmortales y para 1000 inmortales, en este caso vemos que el invariante se sigue cumpliendo. Para el caso de 10000 inmortales es imposible realizar esta ejecución ahora pues es demasiada la información que arroja el programa la cual nuestras computadoras no soportan.

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es 	  necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto

	Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.

	**Respuesta:** Es posible que se produzca una condicion Carrera, debido a que si eliminamos los inmortales a medida que mueren, mientras sigue en ejecución a aplicación, por la rapidez del programa y entre mas inmortales hayan, es posible que el programa intente acceder al mismo indice y remueva otro inmortal que tenga toda su vida. Lo que ocasiona modificaciones al invariante y resultados inesperados.

	Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.

	![](img/inmortal12.png)
	![](img/inmortal13.png)
	![](img/inmortal14.PNG)
	
	**Respuesta:** Con el uso de la coleccion recurrente CopyOnWriteArrayList logramos sin necesidad de sincronización que el codigo se ejecutara sin condiciones carrera o errores inesperados.La solución consistio en colocar una bandera en el metodo fight para cuando el segundo luchador llega su vida a cero de forma que este temrine su ejecución. Para el momento que el jugador le de click al metodo de Pause, el metodo clearlist va a eliminar todos los inmortales cuya vida es cero.

11. Para finalizar, implemente la opción STOP.

	![](img/stop.png)
	![](img/stopmethod.png)

	**Respuesta:** Para la ejecución de los hilos con el metodo stop, mientras que en el botón vamos a limpiar la lista, el texto y vamos a habilitar el botón start para una nueva ejecución.

<!--
### Criterios de evaluación

1. Parte I.
	* Funcional: La simulación de producción/consumidor se ejecuta eficientemente (sin esperas activas).

2. Parte II. (Retomando el laboratorio 1)
	* Se modificó el ejercicio anterior para que los hilos llevaran conjuntamente (compartido) el número de ocurrencias encontradas, y se finalizaran y retornaran el valor en cuanto dicho número de ocurrencias fuera el esperado.
	* Se garantiza que no se den condiciones de carrera modificando el acceso concurrente al valor compartido (número de ocurrencias).


2. Parte III.
	* Diseño:
		- Coordinación de hilos:
			* Para pausar la pelea, se debe lograr que el hilo principal induzca a los otros a que se suspendan a sí mismos. Se debe también tener en cuenta que sólo se debe mostrar la sumatoria de los puntos de vida cuando se asegure que todos los hilos han sido suspendidos.
			* Si para lo anterior se recorre a todo el conjunto de hilos para ver su estado, se evalúa como R, por ser muy ineficiente.
			* Si para lo anterior los hilos manipulan un contador concurrentemente, pero lo hacen sin tener en cuenta que el incremento de un contador no es una operación atómica -es decir, que puede causar una condición de carrera- , se evalúa como R. En este caso se debería sincronizar el acceso, o usar tipos atómicos como AtomicInteger).

		- Consistencia ante la concurrencia
			* Para garantizar la consistencia en la pelea entre dos inmortales, se debe sincronizar el acceso a cualquier otra pelea que involucre a uno, al otro, o a los dos simultáneamente:
			* En los bloques anidados de sincronización requeridos para lo anterior, se debe garantizar que si los mismos locks son usados en dos peleas simultánemante, éstos será usados en el mismo orden para evitar deadlocks.
			* En caso de sincronizar el acceso a la pelea con un LOCK común, se evaluará como M, pues esto hace secuencial todas las peleas.
			* La lista de inmortales debe reducirse en la medida que éstos mueran, pero esta operación debe realizarse SIN sincronización, sino haciendo uso de una colección concurrente (no bloqueante).

	

	* Funcionalidad:
		* Se cumple con el invariante al usar la aplicación con 10, 100 o 1000 hilos.
		* La aplicación puede reanudar y finalizar(stop) su ejecución.
		
		-->

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.
