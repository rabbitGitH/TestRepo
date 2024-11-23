// count the contacts related to the account NoOfContact__c

trigger ContactTrigger on Contact (after insert, after update, after delete, after undelete){
	
	set<id> accountIds = new set<id>();
	
	if(trigger.isAfter && (trigger.isInsert || trigger.isUndelete)){
		for(Contact c:trigger.new){
			if(c.AccountId != null){
				accountIds.add(c.accountId);
			}
		}
	}
	
	if(trigger.isAfter && trigger.isUpdate){
		for(Contact c:trigger.new){
			if(c.accountId != null && c.accountId != trigger.oldMap.get(c.id).accountId){
				accountIds.add(c.accountId);
				accountIds.add(trigger.oldMap.get(c.id).accountId);
			}
		}
	}
	
	if(trigger.isAfter && trigger.isDelete){
		for(Contact c:trigger.old){
			if(c.accountId != null){
		    	accountIds.add(c.accountId);
			}
		}
	}
	
	if(!accountIds.isEmpty()){
		
		List<account> updateAccounts = new List<account>();
		List<AggregateResult> result = [select accountId accId, COUNT(id) conCount from contact where accountId in : accountIds group by accountId];
		
		if(!result.isEmpty()){
			
			for(AggregateResult r:result){
				
				account acc = new account();
				acc.NoOfContact__c = (decimal)r.get('conCount');
				acc.id = (string)r.get('accId');
				updateAccounts.add(acc);
			}
		}
		
		if(!updateAccounts.isEmpty()){
			
			try{
				    update updateAccounts;
			}
			
			catch(exception ex){
				system.debug('Found some errors during account update--->'+ex.getMessage());
			}
		}
		
	}
	
}