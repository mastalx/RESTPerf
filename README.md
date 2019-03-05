Performance Utilities
========================

 * RestPerf
 * WadoPerf


RestPerf
---------------------

### Examples

```
usage: RestPerf
 -experiment <arg>   name of experiment
 -help               print this message
 -ieuser <arg>       i-engine user
 -password <arg>     rest2 password
 -pid <arg>          pid - patient id
 -savefile <arg>     store returned file
 -threads <arg>      number of test threads
 -totalruns <arg>    number of test runs
 -url <arg>          rest2 endpoint url
 -user <arg>         rest2 user
```

#### Examples

`RestPerf.bat -url http://10.5.69.18:7501/rest2/objects?q=PAV%20Dokumentenliste -user PAT_ARCHIVE_VIEWER_USER -passowrd *** -ieuser TIESUMSE -pid 3013955  -threads 5 -totalruns 15 -savefile false`



WadoPerf
---------------------

```
usage: WadoPerf
 -experiment <arg>   name of experiment
 -help               print this message
 -input <arg>        file input for csv - studyUid,seriesUid,objectUid)
 -savefile <arg>     store returned file
 -threads <arg>      number of test threads
 -url <arg>          rest2 endpoint url
``` 

#### Examples

`WadoPerf.bat -url http://localhost/wado/1234 -experiment test -savefile true -threads 5 -input C:\dev\ie-github\RESTPerf\input-instances.csv`

