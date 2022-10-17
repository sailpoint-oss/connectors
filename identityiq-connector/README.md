![SailPoint logo](https://media.licdn.com/mpr/mpr/shrink_200_200/AAEAAQAAAAAAAAdcAAAAJDAwZDFlZDQ5LWNhOWYtNDhmZS1iYTNjLTE3NGI3ZTI4ZDc3Ng.png)

# Overview
SailPoint IdentityIQ connector is used to aggregate identities from SailPoint IdentityIQ, as well as perform basic identity-level provisioning actions.

## Supported Features

The IdentityIQ connector provides the ability to read and write identities from the IdentityIQ system via a SCIM 2.0 interface. The connector supports the following features:

* Account Management
	* Manages IdentityIQ Identities as Accounts
	* Aggregation, Refresh Account
	* Create, Update, Delete
	* Enable, Disable 
	* Change Password
	* Add and Remove Entitlements

## Supported Managed Systems 
SailPoint IdentityIQ connector supports IdentityIQ version 7.0p3 and higher.

## Prerequisites

In order for this to be used the following conditions must be valid:

* IdentityIQ 7.0p3 or higher must be running.
* IdentityNow VAs must have network connectivity over HTTP or HTTPS on the target port(s) that IdentityIQ application servers are running on.
* IdentityNow has the IdentityIQ Connector installed.
* IdentityIQ has an identity configured with a valid password and correct administrator permissions as defined in this section.
 
## Administrator Permissions 

The identity that is being logged in as must have the following Capabilities within IdentityIQ:

* SCIMExecutor
* WebServicesExecutor

## Configuration Parameters

Parameters                | Description
--------------------------|----------------------------------------------------
Base URL (host)           | This is the URL of the IdentityIQ application server. e.g. http://host.example.com:8080/identityiq
User (user)               | This IdentityIQ identity logging in. *Note: This should be the identity.name attribute.* e.g. spadmin
Password (password)       | This IdentityIQ identity's password to log in.
Page Size (pageSize)      | This is the number of identites to fetch during aggregation. Optional. Default is 100.
Filter (accountFilter)      | Filter string used to filter out accounts. Must conform to SCIM filtering criteria. Optional. 


# Schema Attributes

The application schema is used to configure the objects returned from a connector. When a connector is called, the schema is supplied to the methods on the connector interface. This connector currently supports the following type of objects:

* Account

The attributes listed below are part of the SCIM 2.0 Standard.  For more information see <http://www.simplecloud.info>.

## Account Attributes

Attribute                 | Description
--------------------------|----------------------------------------------------
id                        |  IIQ identity.id.  Required. 
userName                  |  IIQ identity.name.  Required.
externalId                |  
displayName               |  IIQ identity.displayName.
nickName                  |  
profileUrl                |  
title                     |  
userType                  |  
preferredLanguage         |  
locale                    |  
timezone                  |  
active                    |  IIQ identity.inactive.
name.formatted            |  IIQ identity.displayName.
name.familyName           |  IIQ identity.lastname.
name.givenName            |  IIQ identity.firstname.
name.middleName           |  
name.honorificPrefix      |  
name.honorificSuffix      |  
employeeNumber            |  
costCenter                |  
organization              |  
division                  |  
department                |  
manager.id                |  IIQ identity.manager.id.
manager.displayName       |  IIQ identity.manager.displayName.
emails.home.value         |  
emails.work.value         |  IIQ identity.email.
phoneNumbers.work.value   |  
phoneNumbers.home.value   |  
ims                       |   
photos.photo              |  
photos.thumbnail          |  
addresses.work.formatted  |  
addresses.work.streetAddress  |  
addresses.work.locality   |  
addresses.work.region     |  
addresses.work.postalCode |  
addresses.work.country    |  
addresses.home.formatted  |  
addresses.home.streetAddress  |  
addresses.home.locality   |  
addresses.home.region     |  
addresses.home.postalCode |  
addresses.home.country    |  
groups                    |  
entitlements              |  
roles                     |  
meta.created              |  IIQ identity.created.
meta.lastModified         |  IIQ identity.modified.
meta.location             |  
meta.version              |  

**Note**: Additional attributes can be dynamically added by using `attribute.` syntax. For instance, to add `myAttribute`, you would add `attribute.myAttribute` to the account schema, and it would be brought in automatically.

# Connector Installation

TBD

# Troubleshooting
TBD
