№ Final project for the course "Artificial Intelligence"
Moments (criteria):
* Android app (Java app)
* Voice control
* Payload ("To-Do List" is selected as the payload)  

Available voice commands:
* "Создать заметку" sends to the manual note creation window
* "Слушать" starts a new RecognizerIntent.ACTION_RECOGNIZE_SPEECH, the contents of which will be recorded as a new note
* "Найти [number]" opens note № [number] to manually edit the content
* "Выполнить [number]" marks note № [number] as "Completed" (if the note has already been filled in, it becomes active again)
* "Удалить [number]" removes note № [number] from the list  

Moreover:
* there is a usual manual control
* when using voice control, clearly state the commands