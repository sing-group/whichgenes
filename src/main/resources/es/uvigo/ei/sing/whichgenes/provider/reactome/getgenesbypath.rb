branch(:BRANCH_DUPLICATED, :ORDERED){
	pipe {
		decorate(:head=>'http://reactome.org/ReactomeRESTfulAPI/RESTfulWS/pathwayComplexes/')
		wget
		match(:pattern=>'UniProt:(.*?)[\" ]')
	}
	pipe {
		decorate(:head=>'http://reactome.org/ReactomeRESTfulAPI/RESTfulWS/pathwayParticipants/')
		wget
		match(:pattern=>'\"dbId\" : ([0-9]*)')
		decorate(:head=>'http://reactome.org/ReactomeRESTfulAPI/RESTfulWS/queryById/DatabaseObject/')
		wget(:headers=>'{"Accept":"application/xml"}')
                branch(:BRANCH_DUPLICATED, :SCATTERED){
                  pipe {
                    xpath(:htmlClean=>'false', :XPath=>'//hasMember/dbId/text()')                                   
                    decorate(:head=>'http://reactome.org/ReactomeRESTfulAPI/RESTfulWS/queryById/DatabaseObject/')
                    wget
                    match(:pattern=>'UniProt:(.*?)[\" ]')
                  }
                  match(:pattern=>'UniProt:(.*?)[ <]')
                }
	}		
}
