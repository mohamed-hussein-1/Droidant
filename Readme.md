this is an android application to connect to arduino through bluetooth
the application works by waiting for a command to excute either through voice or text
for voice , the recognition part is done by google api
for knowing which command should be excuted we use an online api called `api` to identify the type of command
it sends the command to arduino microcontroller through bluetooth connection
current commands include :-
switching light on/off
switch tv on/off
getting room temperature