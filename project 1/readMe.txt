Project 1 Group 7

Mingyang Zheng (mz2594)   Huafeng Shi (hs2917)

1. File List
    (1) code
        query.py: the entrance of the program
        InformationRetrieval.py: the body of the program
        RocchioAlgorithm.py: the Rocchio Algorithm
        proj1-stop.txt: the stop words used in this program
        
    (2) transcript: the runs of your program on the 3 test cases 
    (3) readMe.txt: the README file

2. Run the program

    (1) prerequisite: 
        python3
        googleapiclient: $ pip install --upgrade google-api-python-client
        numpy:           $ pip install numpy
        sklearn:         $ pip install sklearn
    
    
    (2) run the program:
        $ python3 query.py -query {your query}
        In this situation, the program will use the default api_key 
        (AIzaSyAHzQAbQFJmGyElhnh_VVFay_ECunRqVoE), engine id (009650898989487274447:ghd3zgarfa4),
        and precision (0.9)
        
        You can also input these parameters manually:
        $ python3 query.py -query {your query} -api_key {your api key} -cse_id {your engine id} -precision {number between 0 and 1}
    
3. Internal design
    step 1: use google search API to get the top 10 results from the query
    step 2: extract 'snippet', 'link' and 'title' of each result and encapsulate
            them into a class called SimpleSearchResult
    step 3: if search result numbers < 10, then stop search and exit program;
            else do relevance feedback with user
    step 4: make judgement about whether augment query is needed:
            if current_precision > wanted_precision, then finish program and exit
            else if current_precision == 0, stop program
            else we need augment query, do step 5
    step 5: query modification and augmentation (see below), then back to step 1
    
    
4. query-modification method 
    step 1: For each of the searched result, combine the snippet and title as a document;
            treat the query as one document, then merge them as a document list
    step 2: calculate the tf-idf vector of each of these documents after removing the stop words
    step 3: Use Rocchio Algorithm to get the modified query vector according to 
            user's feedback. In this grogram, we set alpha = 1, beta = 0.75, gamma = 0.15
    step 4: Sort each dimension of this vecter in descending order, choose two words 
            corresponding to the largest dimension value and append them to the query. 
            If the chosen words already exit in the query, append word with third-largest
            value, and so on.
            
5. api_key: AIzaSyAHzQAbQFJmGyElhnh_VVFay_ECunRqVoE, 
   engine id: 009650898989487274447:ghd3zgarfa4
        
            

        