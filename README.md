# TxtToMail

A customizable java tools to send HTML mail from command line. With attachments, images, arrays, inside. 

[![Build Status](https://travis-ci.org/matgou/txttomail.svg?branch=master)](https://travis-ci.org/matgou/txttomail)
[![codecov.io](https://codecov.io/github/matgou/txttomail/coverage.svg?branch=master)](https://codecov.io/github/matgou/txttomail?branch=master)

## Usage via command line :

### All in one command :

You can send email via one command :
```bash
java -jar TxtToMail.jar --send -TO "matgou@kapable.info" -SUBJECT "this is a test" -TEXT "Hy, <br/> This is a test mail!"
```

### Step by step command :

Or you can run multiple command (for shell script usage)

* First step is to produce a file "mail.template" containing mail information
```bash
java -jar TxtToMail.jar -i mail.template -FROM matgou@kapable.info
java -jar TxtToMail.jar -i mail.template -TO test@kapable.info
java -jar TxtToMail.jar -i mail.template -SUBJECT "This is a test"
java -jar TxtToMail.jar -i mail.template -TEXT "Hy, <br/>"
java -jar TxtToMail.jar -i mail.template -TEXT "This is a test mail !"
java -jar TxtToMail.jar -i mail.template -ARRAY "src/main/resources/tab1.csv"
java -jar TxtToMail.jar -i mail.template -PJ "src/main/resources/tab1.csv"
```

* Second step to send mail, type this command to keep copy of mail in "output.eml" file
```bash
java -jar TxtToMail.jar -i mail.template -o output.eml -send
```

## Configuration

You can specify some extra option like smtp-host by creating a .properties file and use it with "-c" option :
* A config.properties file :
```
mail.smtp.host=smtp.myisp.com
logfile=mail.log
```
* Send the mail previously prepared :
```bash
java -jar TxtToMail.jar -c config.properties -i mail.template -o output.eml -send
```

## Customizing

TODO

## Building from source

To build from source use mvn :
```bash
mvn clean test package
```
