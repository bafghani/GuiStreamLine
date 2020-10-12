EXTRA CREDIT
OPTION 1: PLAYER ANIMATION
OPTION 2: BUMP ANIMATION

PROGRAM DESCRIPTION
This program creates an interactive display for the streamline game we created
in PSA 3, Streamline. It uses the RoundedSquare file to create RoundedSquare 
shapes that represent the player, goal, and obstacle tiles. These are all given
their own unique colors. Circle objects are used to represent trail spaces. 
GuiStreamline.java implements these shapes and adjusts their location to fit
in the screen. The up, left, right, down, keys are used to move the player, 
while u, undos, q, quits, and o, saves the game.

SHORT RESPONSE

UNIX/LINUX QUESTIONS:
1. To create a new directory fooBar with a directory dirDir within it:
   mkdir -p fooBar/dirDir
2. A wildcard character is used to match a specific filename in a directory
   For example, ls -l FILE_NAME?.java matches a single instance of 
   FILE_NAME.java in the current directory.
3. ls -lR to list all files, directories, and all sub-files/directories.

JAVAFX QUESTIONS
1. One could simply put the call to handle() within the handle method instead
   of creating a handler object to call handle(). 
   Just define setOnKeyPressed to be KeyEvent e, and pass e to the handle method.
   i.e. handleKeyCode(e.getCode());
   This will bypass the nested handle() class, and call MyKeyHandler recursively 
   instead.
2. The Group class organizes all like objects so that operations may be applied to 
   all of them all at once rather than applying changes to each object individually.
   Nesting groups is a way to show a relationship between groups while keeping
   their children (objects of the group) organized.