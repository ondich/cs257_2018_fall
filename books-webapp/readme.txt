Running the books/authors web application
CS257 Software Design
Fall 2018
Jeff Ondich

0. Assumption

Note that I assume you're going to run the
application on perlman.mathcs.carleton.edu. If
not, adjust these instructions accordingly.


1. Set up the database of books and authors

See books_data/readme.txt for instructions.


2. Pick your ports for the API and the website

I'll use 5132 (API) and 5232 (website) as my
examples, since those are available for use by
my account on perlman.mathcs.carleton.edu.
Please use your own ports.


3. Launch the API

    books_api.py perlman.mathcs.carleton.edu 5132


4. Launch the website

    books_website.py perlman.mathcs.carleton.edu 5232 5132

Note that the first port is for the website, and
the second is to tell the website which port the API
is running on.


5. Try it out

Point your browser to

    http://perlman.mathcs.carleton.edu:5232/

Assuming all goes well, you'll be able to click on the
"Get Authors" button and get the list of authors.

