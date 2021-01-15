package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		/** COMPLETE THIS METHOD **/
		if(g.map.get(p1) == null || g.map.get(p2) == null){
			return null;
		}

		Queue<ArrayList<String>> queue = new Queue<>();
		ArrayList<String> path = new ArrayList<>();
		path.add(p1);
		queue.enqueue(path);
		HashSet<String> visited = new HashSet<>();

		while(queue.isEmpty() == false){
			path = queue.dequeue();
			String vertex = path.get(path.size()-1); 
			if(vertex.equalsIgnoreCase(p2)){
				return path;
			}
			else if(visited.contains(vertex) == false){ 
				Person person = g.members[g.map.get(vertex)];
				
				for(Friend friend = person.first; friend != null; friend = friend.next){
					ArrayList<String> newPath = (ArrayList<String>) path.clone(); 
					newPath.add(g.members[friend.fnum].name); 
					queue.enqueue(newPath);
				}
				visited.add(person.name);
			}
		}

		return null;
		
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		/** COMPLETE THIS METHOD **/
		ArrayList<ArrayList<String>> cliques = new ArrayList<>();
		HashSet<String> visited = new HashSet<>();

		for(Person person: g.members){ 
			if(visited.contains(person.name) == false && person.student && person.school.equalsIgnoreCase(school)){ 
				visited.add(person.name); 
				ArrayList<String> clique = new ArrayList<>(); 
				clique.add(person.name);
				Queue<String> queue = new Queue<>();
				queue.enqueue(person.name);

				while(queue.isEmpty() == false) {
					String name = queue.dequeue();
					Person current = g.members[g.map.get(name)];

					for(Friend friend = current.first; friend != null; friend = friend.next){
						current = g.members[friend.fnum];
						if(visited.contains(current.name) == false && current.school != null && current.school.equalsIgnoreCase(school)){
							clique.add(current.name);
							queue.enqueue(current.name);
						}
						visited.add(current.name);
					}
				}

				cliques.add(clique);
			} 
			else{ 
				visited.add(person.name); 
			}

		}

		if(cliques.isEmpty()){
			return null;
		}
		else {
			return cliques;
		}
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION	
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		/** COMPLETE THIS METHOD **/
		int[][] dfsNum = new int[g.members.length][4];
		ArrayList<String> connectors = new ArrayList<>();

		int index = getUnvisitedIndex(dfsNum);
		while (index != -1) {
			dfsNum = dfs(dfsNum, g.members, index, 1); 

			int numFriends = 0;
			for (Friend friend = g.members[index].first; friend != null; friend = friend.next) {
				numFriends++;
			}
			if (numFriends < 2 || dfsNum[index][3] != 0 && dfsNum[index][2]/dfsNum[index][3] > 2) {
				dfsNum[index][3] = 0;
			}
			index = getUnvisitedIndex(dfsNum);
		}

		for(int i = 0; i < dfsNum.length; i++){
			if(dfsNum[i][3] > 0) {
				connectors.add(g.members[i].name);
			}
		}

		if(connectors.isEmpty()) {
			return null;
		}
		else {
			return connectors;
		}
		
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		
	}
	
	private static int[][] dfs(int[][] dfsNum, Person[] members, int index, int dfs) {
		dfsNum[index][0] = dfs;
		dfsNum[index][1] = dfs;
		dfsNum[index][2]++;
		for( Friend friend = members[index].first; friend != null; friend = friend.next){
			if(dfsNum[friend.fnum][2] > 0){
				dfsNum[index][1] = Math.min(dfsNum[index][1], dfsNum[friend.fnum][1]);
				continue;
			}
			dfsNum = dfs(dfsNum, members, friend.fnum, ++dfs);

			int backV = dfsNum[index][1];
			int backW = dfsNum[friend.fnum][1];
			if(backV > backW) {
				dfsNum[index][1] = Math.min(backV, backW);
			}
			else {
				dfsNum[index][3]++;
			}
		}
		return dfsNum;
	}

	private static int getUnvisitedIndex(int[][] dfsNum) {
		for(int i=0; i<dfsNum.length; i++) {
			if(dfsNum[i][2]==0){
				return i;
			}
		}
		return -1;

	}
	
}

