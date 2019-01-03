# PCD_DiffieHellman
Per lo svolgimento dell'eserizio ho deciso di utilizzare una struttura composta da DiffieHellmanTaskBuilder che implementa Supplier<Callable<List<Integer>>> e al suo interno contiene DiffieHellmanTask che implementa Callable<List<Integer>>.
La scelta di implementare l'interfaccia Callable come primitiva di DiffieHellmanTask è dovuta alla necissità di definire dei compiti che producono un risultato.
Per quanto rigurda il metodo crack di DiffieHellman, ho parallelizzato il calcolo dei possibili B^a mod p e dei A^b mod p e il loro confronto avviene in parallelo con tanti thread tanti quanti sono i core disponibili tramite l'utilizzo di un ThreadPoolExecutor.
Infine vengono accodate le varie liste create dai thread e viene ritornata la lista con i possibili valori di a e b.
I tempi di esecuzione del test variano tra i 50~ e 53~ secondi (test eseguiti su Asus N551JX con processore i7-470HQ @ 2.6 GZ).
