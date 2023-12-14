# generate-multilang-console
## Features
+ Create new resources file
+ Append new resources to current resources file
## Supports
+ Android resources string
+ IOS localizable (soon)
## How to use
1. Put xlsx file in the same directory with the application and name file as "multilang.xlsx"
2. The xlsx should have these column: 
  + key: for key string
  + EN: for english definition
  + VI: for vietnamese definition
  + JP: for japanese definition (optional)
  + KR: for korean definition (optional)
3. If you want to appent to your current resources file, your files should be in */current* directory (include all folder with locale have string file)
    Example: Android should have value/string.xml and value-vi/string.xml
4. Run program in **java 11 or above**
5. Get out output at */output* directory, the version with appear with the time you created them

