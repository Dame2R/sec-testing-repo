@echo off
set param0=%~1
set param1=%~2

set stage=%param0%
set owner=%param1%

call cd user-request-service
call deployer.bat %stage% %owner%

call cd ..

call cd user-creation-service
call deployer.bat %stage% %owner%
call cd ..
