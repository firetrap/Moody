Setup Guide
====

# Requirements #
Moody to be able to work with your School

* Moodle >= 2.2
* Rest Protocol enabled in Moodle
* Moodle be able to generate tokens for the users 
* Moodle have the moody-service enable
* The Moody service has all the functions enabled

## 1. Enable web services
_Site Admin > Advanced Features > TICK : enable web services > save changes._


[ ![Image](http://i.imgur.com/fCaNSL5.png"Image title") ](http://i.imgur.com/gAsKy2W.png)

---

## 2. Enable REST protocol
_Site admin > Plugins > Web services > Manage protocols > Enable REST protocol && TICK: Web services documentation > Save changes_


[ ![Image](http://i.imgur.com/lv4cZ4Z.png"Image title") ](http://i.imgur.com/eYk6dpR.png)

---

## 3. Define REST protocol for the roles
_Site admin > Users > Permissions > Define roles > Settings > TICK: Rest Protocol for the roles_


[ ![Image](http://i.imgur.com/NiGd3Ma.png"Image title") ](http://i.imgur.com/5lt1AOX.png)

---

## 4. Create tokens for web/mobile services
_Site admin > Users> Permissions > Define roles > Settings > TICK: Create Tokens for web/mobile services> Save Changes_


[ ![Image](http://i.imgur.com/7ORahp8.png"Image title") ](http://i.imgur.com/tlnHHRm.png)

---

## 5. Enable web services for mobile devices
_Site admin > Plugins > Web services > External Services > TICK: > Save Changes_


[ ![Image](http://i.imgur.com/DsblIgc.png"Image title") ](http://i.imgur.com/DsblIgc.png)

---

## 6. Create the "moody_service"
_Site admin > Plugins > Web services > External Services > Add > Create a new service "moody_service" with no required capabilities, enabled and can download files > Save Changes_


[ ![Image](http://i.imgur.com/oXxXUIy.png"Image title") ](http://i.imgur.com/OFbrmrO.png)

---

## 7. Add functions to the service"
_Site admin > Plugins > Web services > External Service > moody_service > Functions > add all non-deprecated functions > save changes_


[ ![Image](http://i.imgur.com/1ppaeoN.png"Image title") ](http://i.imgur.com/EXpIHgX.png)

---

## 8. Config phpMyAdmin
_Server > phpMyAdmin > Search for "mdl_external_service" > Add "moody_service" to the shortname> save&exit_


[ ![Image](http://i.imgur.com/ieAl17w.png"Image title") ](http://i.imgur.com/BlqfW3e.png)





> *And that's it! If everything go has expected your Moodle is configured to work with Moody.*
