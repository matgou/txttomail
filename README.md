# TxtToMail

A customizable java tools to send HTML mail from command line. With attachments, images, arrays, inside. 

[![Build Status](https://travis-ci.org/matgou/txttomail.svg?branch=master)](https://travis-ci.org/matgou/txttomail)

## Usage via command line :

* Produce a file "mail.template" containing mail information
```bash
java -jar TxtToMail.jar -i mail.template -FROM matgou@kapable.info
java -jar TxtToMail.jar -i mail.template -TO test@kapable.info
java -jar TxtToMail.jar -i mail.template -SUBJECT "This is a test"
java -jar TxtToMail.jar -i mail.template -TEXT "Hy, <br/>"
java -jar TxtToMail.jar -i mail.template -TEXT "This is a test mail !"
```

* Send the mail via command and keep copy of mail "output.eml":
```bash
java -jar TxtToMail.jar -i mail.template -o output.eml -send
```

## Configuration

You can specify some extra option like smtp-host with creating a .properties file and use it with "-c" option :
```
mail.smtp.host=smtp.myisp.com
```
