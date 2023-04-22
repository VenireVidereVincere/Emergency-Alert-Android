# Emergency-Alert-Android
Android app written in Kotlin designed to work as an emergency alert for a person in distress to pre-selected contacts.

The UI is meant to be simplistic and straightforward as the intention of the app is for it to function effectively and efficiently in case of an emergency. 

The main activity has 4 buttons, one to request all permissions (opted for a button on top of asking for permissions in context due to some phones removing permissions from apps you haven't used in a while, you don't want to be stuck giving permissions to the app in case of an emergency. You can open it once a while, request the permissions and everything will run smoothly)

The second button is for the user to select an emergency contact. Currently, no limit of contacts is implemented. Will be implemented soon. It uses the android UI in order to select the contact. 

The third button is for the user to manage the existing emergency contacts, you can either delete a contact or add a personalized message to be sent in case of an emergency. 

The last button is the emergency one, which sends out a text message with the last known location to each of the emergency contacts alongside either a default or a personalized message (if one was provided by the user), requesting assistance. 

Pressing the emergency button gives the user a 5 second countdown within which they can cancel their action in case they pressed it accidentally. If cancelled, no messages will be sent out. If not cancelled, the messages are sent after the countdown is over. 

# Future implementations:
- Limit on emergency contacts. 
- Limit on characters for the emergency messages.
- The user will be able to select one of their emergency contacts as a "Trusted contact"
- The trusted contact will receive a link in which they will be able to track the live location of the user.
- The trusted contact feature would be available on a subscription based model.
