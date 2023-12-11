@echo off
set param0=%~1
set param1=%~2

set stage=%param0%
set owner=%param1%

echo stage = %stage%
echo owner = %owner%

if %stage%==dev (set profile=611279215325_PA_DEVELOPER)
if %stage%==int (set profile=318081053039_PA_DEVELOPER)
@Rem if %owner%==ntb (set profile=brandon-aws)

echo profile = %profile%


echo npx sls deploy -s %stage% --param="owner=%owner%" --aws-profile  %profile% --verbose

call npx sls deploy -s %stage% --param="owner=%owner%" --aws-profile  %profile% --verbose
