pipe {replace(:dest=>"&actionTypes=", :sourceRE=>":") | decorate(:head=>"http://ctdbase.org/tools/batchQuery.go?q=&inputType=chem&inputTerms=", :tail=>"&format=tsv&action=download&report=cgixns") | wget | match(:dotAll=>"false", :pattern=>"(.*?)\\n") | match(:pattern=>"[^\\t]*\\t[^\\t]*\\t[^\\t]*\\t[^\\t]*\\t([^\\t]*).*?9606.*?")}
