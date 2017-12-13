Project 3 Group 7

Mingyang Zheng (mz2594)   Huafeng Shi (hs2917)

1. File List
    (1) code
        App.java: the entrance of the program, and the basic logic of the program
        Apriori.java: Apriori realization
        DataMiner.java: find association rules
        util.BitSetMapper.java: a mapper between itemSet of string and BitSet
        util.DatasetGenerator.java: generate INTEGRATED-DATASET from original-dataset
        models.AssociationRule.java: the model, containing fields LHS, RHS, confidence, support
        models.FrequentItemset.java: the model, containing fields items, support
        
    (2) INTEGRATED-DATASET.csv
    (3) output.txt: the runs of your program on the test case: minsupport=0.01 minconfidence=0.5
    (4) example-run.txt: listing all the frequent itemsets as well as association rules for that run, the same as output.txt 
    (5) readMe.txt: the README file


2. Run the program

    (1) prerequisite: 
        JDK 1.8
		$ sudo apt-get install default-jdk
		Maven
		$ sudo apt-get install maven
    
    
    (2) run the program:
    	$ mvn install

        $ java -jar target/cs6111project3-1.0-SNAPSHOT.jar
        In this situation, the program will use the default INTEGRATED-DATASET 
        ("INTEGRATED-DATASET.csv"), minSupport (0.01), minConfidence(0.5)

         $ java -jar target/cs6111project3-1.0-SNAPSHOT.jar {INTEGRATED-DATASET} {minSupport} {minConfidence} 
         use above command to specify the parameter you want.
         for example:
         $ java -jar target/cs6111project3-1.0-SNAPSHOT.jar "INTEGRATED-DATASET.csv" 0.01 0.5


3. Detailed description
	
	(1) DataSet:
	311 Service Requests: Based on 311 Service Requests from 2010 to Present
	https://data.cityofnewyork.us/Social-Services/311-Service-Requests/fvrb-kbbt

	(2) Procedure to map the original NYC Open Data data set(s) into INTEGRATED-DATASET
		step 1: Choose the columns we need before downloading. The columns we choose are Created Date,Agency,Complaint Type,Location Type,Street Name,City
		step 2: Resample the dataset randomly, the sample rate is 1: 400
		setp 3: change the column "Date" into the month the date belongs to.
		Note: step2 and step3 are realized in code util.DatasetGenerator.java: generate INTEGRATED-DATASET from original-dataset

	(3) What makes the choice of INTEGRATED-DATASET file compelling?
		The INTEGRATED-DATASET file contains 311 Service Requests in different months, complaint type and location. We can utilize it to find many interesting results by association rules, such as, an agency usually solve what kind of compliants? A particular kind of complaint always happens in which month, in which city? what's the relation between the complaint type and location type? an so on. 


4. internal design
	(1) Item Sets storage
	The most tricky part of this project is the way to store item set. Usually, the item sets can be stored in HashSet or TreeSet. However, in this project, we choose to use BitSet. Notice the Algorithm Apriori requires item to be in order (such as lexicographic order). TreeSet can guarantee order, but takes more time for the contains method; HashSet take constant time for contain method of each item, but cannot guarantee items in order. Moreover, the time for containsAll method is proportional to the size of the item set. To make containsAll method in constant time while keep items in order, BitSet comes into my mind. To check whether a market basket contains all the item in the item set, we can use bit manipulation: itemSetClone.and(marketBasket).equals(itemSet), which takes constant time. We use util.BitSetMapper.java to map the item set into a BitSet. The third advantage of using BitSet is it can save much space for big data set.

	(2) Data mining process

	step 1: find Frequent Itemsets with Apriori above min support
	This step is realized by Algorithm Apriori:
		 substep 1. Apriori Candidate Generation
	        (1) join step
	        In join step, to check two item sets only different in the last item, we check whether the BitSets of the two item set only different in the last set-to-1 bit.
	        (2) prune step
	        Change each set-to-1 bit to 0 and check whether last round contains that BitSet.
		substep 2.  retain the k-itemsets above min support
		Use "and" bit manipulation with market bastket as mentioned in previous part "Item Sets storage" to check whether a market basket contains that item set.

    setp 2: generate association rules that satisfy the min confidence
    Seperate the itemset into LHS and RHS then calculate the confindence. keep the association rule above minConfidence.

5. Command line specification 

In example-run.txt, we use minSupport (0.01), minConfidence(0.5), the command is 
$ java -jar target/cs6111project3-1.0-SNAPSHOT.jar "INTEGRATED-DATASET.csv" 0.01 0.5

We can find many intersting results from it:

[HEAT/HOT WATER] => [RESIDENTIAL BUILDING] (Conf: 1.0, Supp: 0.09668602664844551)
This indicate HEAT/HOT WATER always happens in RESIDENTIAL BUILDING

[BRONX, HEAT/HOT WATER] => [HPD] (Conf: 1.0, Supp: 0.03245644004099761)
This indicate in BRONX, HEAT/HOT WATER problem always be solved by HPD (department of housing preservation and development)

[PAINT/PLASTER] => [RESIDENTIAL BUILDING] (Conf: 1.0, Supp: 0.02784420908780321)
[RESIDENTIAL BUILDING, PAINT/PLASTER] => [HPD] (Conf: 1.0, Supp: 0.0278442090878)
This indicates PAINT/PLASTER problem always happens in RESIDENTIAL BUILDING and solved by HPD

[NEW YORK, Noise - Residential, Residential Building/House] => [NYPD] (Conf: 1.0, Supp: 0.019644687393235393)
This shows in new york, residential noise in residential building/house always be solved by NYPD

[December, HPD] => [HEAT/HOT WATER] (Conf: 0.5485714285714286, Supp: 0.016399043389135635)
This shows half of problems HPD solved in SDecember is related to HEAT/HOT WATER


6. Additional information
This code only runs on JDK 1.8





