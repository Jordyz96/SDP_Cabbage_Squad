# SDP_Cabbage_Squad
System.Console.WriteLine("What up my fellow Cabbagarians!"); 

# Problem Statement
Develop a web app or mobile app to record professional journal entries

# Requirements
All journal entries are immutable. That is, they can't be modified and can't be deleted. Any modifications, for example, to edit or update an entry, must retain the original entry but not show it or display it unless there is a specific request to show the full history of the entry.

The journal must be secure, accessible only by its owner and author.

[Below are the listed functions in the assignment document, refer to Trello & Google Drive for a more thorough list of functions]
Required functions:
* Register an author (a user)
* Sign in to the journal system.
* Start a new journal.
* View a table of all entries in a journal.
* Delete a journal entry.
* Flagg an entry as "hidden" and remove it from the normal table of entries display.
* Toggle the table of journal entries to show only active entries or all entries, including hidden and entries flagged for deletion
* Record a new journal entry.
* View an individual journal entry.
* Search for a journal entry by keyword and display a table of matching entries
* Display a table of journal entries recorded on a nominated date.
* Dispay a table of journal entries between nominated dates.
* Modify a journal entry by replacing an entry with its modified version.
* Display the history of a journal entry.

# Communication amongst team members
As a team, we have decided to use Facebook Messenger to keep in contact with each other when face-to-face contact is restricted. 
We also have a Google Drive folder for collaboration on documents while being updated in real-time. 

# Development tools
Android Studio will be utilised to achieve the problem statement. Firebase will provide us with the functions of a real-time database, server management, authentication and storage. 

# Coding standards
1) Each individual method MUST be documented instrinctly.
    /*
    --> //Explain what this method does
    */
    private void deleteEntry(Object Entry)
    {
        //Code
    }
   
   
2) Limit of 80 characters per line for readability

3) Also indent nested code

4) Limit of 4 nested condition statements

5) Add white spaces between blocks of code

6) Utlise camel casing
