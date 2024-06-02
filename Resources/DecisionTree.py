#//
#// A Simple data set from Programming collective Intelligence
#//
#// Columns are
#//   REferrer,Location,ReadFAQ,Pages Viewed, Service Chosen
#//
#
#
import json
import ast
my_data2=[['slashdot','USA','yes',18,'None'],
        ['google','France','yes',23,'Premium'],
        ['digg','USA','yes',24,'Basic'],
        ['kiwitobes','France','yes',23,'Basic'],
        ['google','UK','no',21,'Premium'],
        ['(direct)','New Zealand','no',12,'None'],
        ['(direct)','UK','no',21,'Basic'],
        ['google','USA','no',24,'Premium'],
        ['slashdot','France','yes',19,'None'],
        ['digg','USA','no',18,'None'],
        ['google','UK','no',18,'None'],
        ['kiwitobes','UK','no',19,'None'],
        ['digg','New Zealand','yes',12,'Basic'],
        ['slashdot','UK','no',21,'None'],
        ['google','UK','yes',18,'Basic'],
        ['kiwitobes','France','yes',19,'Basic']]
ret_val = "FALSE"
my_data = ast.literal_eval(INPUT_DATA) ; #eval(INPUT_DATA);
# ------ class Decision Node
# ------- Acts as the storage for Data Node items
#

class DecisionNode:
	def __init__(self,col=-1,value=None,results=None,tb=None,fb=None):
		self.col=col
		self.value=value
		self.results=results
		self.tb=tb
		self.fb=fb


#
# A Function to Divide the Data Set into
# Two
# Params :- Rows , Column and Value
# Splits Categorical and Numerical Attributes
# Always Does a Binary Split
#
def DivideSet(rows,column,value):
   split_function=None
   #----- if data type is int or float/double
   if isinstance(value,int) or isinstance(value,float):
      split_function=lambda row:row[column]>=value
   else:
      split_function=lambda row:row[column]==value

   # Divide the rows into two sets and return them
   set1=[row for row in rows if split_function(row)]
   set2=[row for row in rows if not split_function(row)]
   return (set1,set2)

# ---- Count unique rows
#
#
#

def UniqueCounts(rows):
   #------- Result is an empty dictionary
   results={}
   #------- Iterate Each Rows
   for row in rows:
      # The result is the last column
      r=row[len(row)-1]
      if r not in results: results[r]=0
      results[r]+=1
   return results


##
##
##

# Probability that a randomly placed item will
# be in the wrong category
def giniimpurity(rows):
  total=len(rows)
  counts=uniquecounts(rows)
  imp=0
  for k1 in counts:
    p1=float(counts[k1])/total
    for k2 in counts:
      if k1==k2: continue
      p2=float(counts[k2])/total
      imp+=p1*p2
  return imp

# Entropy is the sum of p(x)log(p(x)) across all
# the different possible results
def entropy(rows):
   from math import log
   log2=lambda x:log(x)/log(2)
   results=UniqueCounts(rows)
   # Now calculate the entropy
   ent=0.0
   for r in results.keys():
      p=float(results[r])/len(rows)
      ent=ent-p*log2(p)
   return ent



#
#------- Print Tree
def PrintTree(tree,indent='',code_list=[]):
   # Is this a leaf node?
   if tree.results!=None:
     if ( len(tree.results) == 1 ):
      for key in tree.results.keys():
       #print (indent + "return " +"\""+ key + "\"")
       code_list.append(indent + "return " +"\""+ key + "\";" )
   else:
      # Print the criteria
      #print (str(tree.col)+':'+str(tree.value)+'? ')
      # Draw the condition string
      #draw.text((x-20,y-10),str(tree.col)+':'+str(tree.value),(0,0,0))
      str_out = ""
      if isinstance(tree.value,int) or isinstance(tree.value,float):
        str_out = "IF ( " + "Var_"+str(tree.col)+ " >= " + str(tree.value) + ") THEN"
        code_list.append(indent +str_out)
      else:
        str_out = "IF (" + "Var_"+str(tree.col)+ " == " + "\""+tree.value+"\"" + ") THEN"
        code_list.append(indent + str_out)
      # Print the branches
      #print (indent+'T->',)
      PrintTree(tree.tb,indent+'  ',code_list)
      code_list.append(indent + "ELSE  " + "\n" )

      #print (indent+'F->')
      PrintTree(tree.fb,indent+'  ',code_list)
      code_list.append(indent + "ENDIF  ")

#------- Print Tree
def CollectVariables(tree,indent,ret_val,pydict_declare,pydict_assign):
   # Is this a leaf node?
   if tree.results!=None:
     if ( len(tree.results) == 1 ):
      for key in tree.results.keys():
       eat_out = ""
   else:
      str_out = ""
      if isinstance(tree.value,int):
        str_out = "NUMERIC " + "Var_"+str(tree.col)+ ";"
	pydict_declare["Var_"+str(tree.col)]=str_out
	str_out = "Var_"+str(tree.col)+" = "+ 'CMD_GET_D('+str(tree.col) + ',ARGS);'
	pydict_assign[ "Var_"+str(tree.col)] = str_out
      elif isinstance(tree.value,float):
        str_out = "NUMERIC " + "Var_"+str(tree.col)+ ";";
	pydict_declare["Var_"+str(tree.col)]=str_out
	str_out = "Var_"+str(tree.col)+" = "+ 'CMD_GET_D('+str(tree.col) + ',ARGS);'
	pydict_assign[ "Var_"+str(tree.col)] = str_out
      else:
        str_out = "STRING " + "Var_"+str(tree.col)+ ";"
	pydict_declare["Var_"+str(tree.col)]=str_out
	str_out = "Var_"+str(tree.col)+" = "+ 'CMD_GET_S('+str(tree.col) + ',ARGS);'
	pydict_assign[ "Var_"+str(tree.col)] = str_out
      CollectVariables(tree.tb,indent+'   ',ret_val,pydict_declare,pydict_assign)
      CollectVariables(tree.fb,indent+'    ',ret_val,pydict_declare,pydict_assign)

def GetWidth(tree):
  if tree.tb==None and tree.fb==None: return 1
  return GetWidth(tree.tb)+GetWidth(tree.fb)

def GetDepth(tree):
  if tree.tb==None and tree.fb==None: return 0
  return max(GetDepth(tree.tb),GetDepth(tree.fb))+1


def buildtree(rows,scoref=entropy):
  if len(rows)==0: return decisionnode()
  current_score=scoref(rows)

  # Set up some variables to track the best criteria
  best_gain=0.0
  best_criteria=None
  best_sets=None

  column_count=len(rows[0])-1
  for col in range(0,column_count):
    # Generate the list of different values in
    # this column
    column_values={}
    for row in rows:
       column_values[row[col]]=1
    # Now try dividing the rows up for each value
    # in this column
    for value in column_values.keys():
      (set1,set2)=DivideSet(rows,col,value)

      # Information gain
      p=float(len(set1))/len(rows)
      gain=current_score-p*scoref(set1)-(1-p)*scoref(set2)
      if gain>best_gain and len(set1)>0 and len(set2)>0:
        best_gain=gain
        best_criteria=(col,value)
        best_sets=(set1,set2)
  # Create the sub branches
  if best_gain>0:
    trueBranch=buildtree(best_sets[0])
    falseBranch=buildtree(best_sets[1])
    return DecisionNode(col=best_criteria[0],value=best_criteria[1],
                        tb=trueBranch,fb=falseBranch)
  else:
    return DecisionNode(results=UniqueCounts(rows))


def classify(observation,tree):
  if tree.results!=None:
    return tree.results
  else:
    v=observation[tree.col]
    branch=None
    if isinstance(v,int) or isinstance(v,float):
      if v>=tree.value: branch=tree.tb
      else: branch=tree.fb
    else:
      if v==tree.value: branch=tree.tb
      else: branch=tree.fb
    return classify(observation,branch)




#---------------------  Main
#print (DivideSet(my_data,2,'yes'))

#//------------- Build a CART TREE
tree = buildtree(my_data)

#print (classify(['(direct)','USA','yes',5],tree))

#print ("Hello World....")
#drawtree(tree,jpeg='treeview.jpg')
pydict_declare = {}
pydict_assign = {}
code_list = []
ret_val2=''
indent2 = '';
code_list.append("FUNCTION STRING CMD_GET_S(NUMERIC OFFSET, STRING s )")
code_list.append("     return s; ")
code_list.append("END")
code_list.append("FUNCTION NUMERIC CMD_GET_I(NUMERIC OFFSET,STRING s )")
code_list.append("     return 1; ")
code_list.append("END")
code_list.append("FUNCTION NUMERIC CMD_GET_D(NUMERIC OFFSET, STRING s )")
code_list.append("     return 0.0; ")
code_list.append("END")
code_list.append("FUNCTION BOOLEAN CMD_GET_B(NUMERIC OFFSET,STRING s )")
code_list.append("     return TRUE; ")
code_list.append("END")
#code_list.append("FUNCTION INTEGER MAIN(INTEGERSTRING str )")
#code_list.append("    return 0;")
#code_list.append("END")
code_list.append("FUNCTION STRING PREDICT(STRING ARGS)"+"\n")
CollectVariables(tree,indent2,ret_val2,pydict_declare,pydict_assign)
for v in pydict_declare.values():
    code_list.append(v)
for c in pydict_assign.values():
    code_list.append(c)
#print(pylist)
PrintTree(tree,'',code_list)
code_list.append("END")

result_value = ''
for temp in code_list:
    result_value = result_value + temp +'\n'

RET_CODE = result_value
RET_VAL = "TRUE"

#print(eval(INPUT_DATA))
#RuleFromTree(tree)