# algebra_helper

Для данного проекта требуются файлы из библиотеки, подключённой к проекту. 

Эта библиотека переопределяет реализуемые стандартным JDK классы, поэтому при получении ошибок типа 
`javax.script.ScriptEngine.eval(String)" because the return value of "javax.script.ScriptEngineManager.getEngineByName(String)" is null` 

- Cтоит удалить (или отключить в настройках IDE) классы JDK пакета `javax.script` сборки `java.scripting`
- Подключённая библиотека сама переопределит классы и встроит их в JDK.

