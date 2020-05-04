package pokemon;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class PokemonDatabase {

	private Connection myConn;
	private Aprende aprende;
	private Conoce conoce;
	
	public PokemonDatabase() {
		
	}

	public boolean connect() {
		String myServer = "localhost:3306";
		String myDb = "Pokemon";
		String myUser = "root";
		String myPass = "12345";
		String myUrl = "jdbc:mysql://" + myServer + "/" +myDb;
		
		try {
			if( myConn == null ||  myConn.isClosed())
			{
				Class.forName("com.mysql.cj.jdbc.Driver");
				myConn = DriverManager.getConnection(myUrl, myUser, myPass); // Is it goooood???
				return false;
			} else {
				
				return true;
			} 
		}catch (ClassNotFoundException e) {
			System.out.println("Error when loading the driver" + e.getMessage());
			return false;
		}catch(SQLException e) {
			System.out.println("SQL Error on opening the connection" + e.getMessage());
			return false;
		}catch(Exception e) {
			System.out.println("Error on opening the connection" + e.getMessage());
			return false;
		}
		
	}

	public boolean disconnect() {
		if(myConn != null) {
			try {
				myConn.close();
				return true;
			} catch (SQLException e)
			{
				System.out.println("SQL Error when closing the connection" + e.getMessage());
				return false;
			}catch(Exception e) {
				System.out.println("Error when closing the connection" + e.getMessage());
				return false;
			}
		}
		
		return true;
	}
	
	

	public boolean createTableAprende() {
		connect();
		Statement st = null;
		String query = "CREATE TABLE IF NOT EXISTS aprende(" +
		                " n_pokedex INT, " +
				        " id_ataque INT, " +
		                " nivel INT, " +
				        " PRIMARY KEY (n_pokedex, id_ataque), " +
		                " FOREIGN KEY (n_pokedex) REFERENCES especie(n_pokedex) " +
		                "    ON DELETE CASCADE ON UPDATE CASCADE, " +
				        " FOREIGN KEY (id_ataque) REFERENCES ataque(id_ataque) " +
		                "    ON DELETE CASCADE ON UPDATE CASCADE" +
				        ");";
				         
		try {		
		 st = myConn.createStatement();   
		 st.executeUpdate(query);
		 if(st!=null) st.close();
		 return true;
		}catch (SQLException e) {
			System.out.println("SQL Error on creating table " + e.getMessage());
			
				try {
					if(st!=null) st.close();
				} catch (SQLException e1) {
					 System.out.println("error when closing the Statement" + e.getMessage());
				}
			return false;
		}catch(Exception e) {
			System.out.println("Error on creating table " + e.getMessage());
			try {
				if(st!=null) st.close();
			} catch (SQLException e1) {
				 System.out.println("error when closing the Statement" + e.getMessage());
			}
			return false;
		}
	}

	public boolean createTableConoce() {
		
       connect();
	   Statement st=null;
		String query = "CREATE TABLE IF NOT EXISTS conoce(" +
				       " n_pokedex INT, " +
		               " n_encuentro INT, " +
		               " id_ataque INT, " +				       
				       " PRIMARY KEY ( n_pokedex, n_encuentro, id_ataque ), " +		                
		               " FOREIGN KEY (n_pokedex) REFERENCES ejemplar(n_pokedex) " +
		               "    ON DELETE CASCADE ON UPDATE CASCADE, " +
		               " FOREIGN KEY (n_encuentro) REFERENCES ejemplar(n_encuentro) " +
		                "    ON DELETE CASCADE ON UPDATE CASCADE, " +
				        " FOREIGN KEY (id_ataque) REFERENCES ataque(id_ataque) " +
				        "    ON DELETE CASCADE ON UPDATE CASCADE " +
				      
				        ");";
				         
		try {		
		 st = myConn.createStatement();   
		 st.executeUpdate(query);
		 if (st != null) st.close();
		 return true;
		}catch (SQLException e) {
			System.out.println("SQL Error on creating table " + e.getMessage());
			
				try {
					if(st != null) st.close();
				} catch (SQLException e1) {
					System.out.println("error when closing the Statement" + e.getMessage());
				}
			return false;
		}catch (Exception e) {
			System.out.println("Error on creating table " + e.getMessage());
			
			try {
				if(st != null) st.close();
			} catch (SQLException e1) {
				System.out.println("error when closing the Statement" + e.getMessage());
			}
		return false;
			
		}
	}

	public int loadAprende(String fileName) {
		connect();
		int result=0;
		PreparedStatement pst=null;
		ArrayList<Aprende> values = aprende.readData(fileName); 
		try {
		    myConn.setAutoCommit(true); // to modifyyyyy
			String query = "INSERT INTO aprende (n_pokedex, id_ataque, nivel) VALUES (?, ?, ?);";
		    pst = myConn.prepareStatement(query);
			
			for(Aprende i:values)
			{
				pst.setInt(1, i.getId_especie());
				pst.setInt(2, i.getId_ataque());
				pst.setInt(3, i.getNivel());
				result+= pst.executeUpdate();	
			}		
		} catch (SQLException e) {
			System.out.println("SQL Error on inserting values in table " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error on inserting values in table " + e.getMessage());
		}
		 
			try {
				if(pst != null) pst.close();
			} catch (SQLException e) {
				System.out.println("error when closing the PreparedStatement" + e.getMessage());
			}
			return result;
	}
	
	

	public int loadConoce(String fileName) {
		connect();
		int result=0;
		PreparedStatement pst = null;
		ArrayList<Conoce> values = conoce.readData(fileName); 
		try {
			myConn.setAutoCommit(false);
			String query = "INSERT INTO conoce (n_pokedex, n_encuentro, id_ataque) VALUES (?, ?, ?);";
		    pst = myConn.prepareStatement(query);
			
			for( Conoce i:values)
			{
				pst.setInt(1, i.getId_especie());
				pst.setInt(2, i.getN_encuentro());
				pst.setInt(3, i.getId_ataque());
				
				result+= pst.executeUpdate();
				
			}
			myConn.commit();
		} catch (SQLException e) {
			System.out.println("SQL Error on inserting values in table " + e.getMessage());
			System.out.println("Rolling back data here....");
			  try{
				 if(myConn!=null)
		            myConn.rollback();
		      }catch(SQLException se2){
		         se2.printStackTrace();
		      }
		} catch (Exception e ) {
			System.out.println("Error on inserting values in table " + e.getMessage());
			System.out.println("Rolling back data here....");
			  try{
				 if(myConn!=null)
		            myConn.rollback();
		      }catch(SQLException se2){
		         se2.printStackTrace();
		      }
		}
	
		try {
			if(pst != null) pst.close();
		} catch (SQLException e) {
			System.out.println("error when closing the PreparedStatement" + e.getMessage());
		}
			return result;
	}

	/*public ArrayList<Especie> pokedex() {
		connect();
		ArrayList<Especie> myArrayList = new ArrayList<Especie>();
		try{
			String query = "select * from especie;";
			PreparedStatement pst = myConn.prepareStatement(query);
			ResultSet rs2=pst.executeQuery();
			
			while(rs2.next()){
				int n_pokedex = rs2.getInt("n_pokedex");
				String nombre = rs2.getString("nombre");
				String descripcion = rs2.getString("descripcion");
				int evoluciona = rs2.getInt("evoluciona");
				myArrayList.add(new Especie(n_pokedex, nombre, descripcion, evoluciona));
			}
		}catch (SQLException esql) {
			System.err.println("Mensaje: " + esql.getMessage());
			System.err.println("Código: " + esql.getErrorCode());
			System.err.println("Estado SQL: " + esql.getSQLState());
			
			return null;
		}
		
		return myArrayList;
	}*/
	
	
	public ArrayList<Especie> pokedex() {
	
		connect();
		ArrayList<Especie> especies = new ArrayList<Especie>();
		Statement st = null;
		ResultSet rs2= null;
		try{
			st = myConn.createStatement();
			String query = "select * from especie;";
			rs2 = st.executeQuery(query);
			
			while(rs2.next()){
				Especie especie = new Especie();
				especie.setN_pokedex(rs2.getInt("n_pokedex"));
				especie.setNombre(rs2.getString("nombre"));
				especie.setDescripcion(rs2.getString("descripcion"));
				especie.setEvoluciona(rs2.getInt("evoluciona"));
				especies.add(especie);
			}
			
			if (rs2 != null) rs2.close();
			if (st != null) st.close();
			
		} catch(SQLException e) {
			System.out.println("SQL error when extracting the data from Especie:" + e.getMessage());
           try {
        	   if (rs2 != null) rs2.close();
           } catch (SQLException e1) {
        	   System.out.println("error when closing the ResultSet" + e.getMessage()); 
           }
			
           try {
        	   if (st != null) st.close();
           } catch (SQLException e1) {
        	   System.out.println("error when closing the Statement" + e.getMessage()); 
           }
           
           return null;
           
		} catch(Exception e) {
			System.out.println("error when extracting the data from Especie:" + e.getMessage());
			
			 try {
	        	   if (rs2 != null) rs2.close();
	           } catch (SQLException e1) {
	        	   System.out.println("error when closing the ResultSet" + e.getMessage()); 
	           }
				
	           try {
	        	   if (st != null) st.close();
	           } catch (SQLException e1) {
	        	   System.out.println("error when closing the Statement" + e.getMessage()); 
	           }
	           
		    return null;
		}
		return especies;
	}

	/*public ArrayList<Ejemplar> getEjemplares() {
		connect();
		ArrayList<Ejemplar> myArrayList = new ArrayList<Ejemplar>();
		try{
			String query = "select * from ejemplar order by n_pokedex, n_encuentro asc;";
			PreparedStatement pst = myConn.prepareStatement(query);
			ResultSet rs2=pst.executeQuery();
			
			while(rs2.next()){
				int n_pokedex = rs2.getInt("n_pokedex");
				int n_encuentro = rs2.getInt("n_encuentro");
				String apodo = rs2.getString("apodo");
				char sexo = rs2.getString("sexo").charAt(0);
				int nivel = rs2.getInt("nivel");
				int infectado = rs2.getInt("infectado");
				myArrayList.add(new Ejemplar(n_pokedex, n_encuentro, apodo, sexo, nivel, infectado ));
			}
		}catch (SQLException esql) {
			System.err.println("Mensaje: " + esql.getMessage());
			System.err.println("Código: " + esql.getErrorCode());
			System.err.println("Estado SQL: " + esql.getSQLState());
			
			return null;
		}
		
		return myArrayList;
	}
*/
	public ArrayList<Ejemplar> getEjemplares() {
		connect();
		ArrayList<Ejemplar> ejemplares = new ArrayList<Ejemplar>();
		Statement st = null;
		ResultSet rs2= null;
		try{
			st = myConn.createStatement();
			String query = "select * from ejemplar order by n_pokedex, n_encuentro asc;";
			rs2 = st.executeQuery(query);
			
			while(rs2.next()){
				Ejemplar ejemplar = new Ejemplar();
				ejemplar.setN_pokedex(rs2.getInt("n_pokedex"));
				ejemplar.setN_encuentro(rs2.getInt("n_encuentro"));
				ejemplar.setApodo(rs2.getString("apodo"));
				ejemplar.setSexo(rs2.getString("sexo").charAt(0));
				ejemplar.setNivel(rs2.getInt("nivel"));
				ejemplar.setInfectado(rs2.getInt("infectado"));
				ejemplares.add(ejemplar);
			}
			
			if (rs2 != null) rs2.close();
			if (st != null) st.close();
			
		} catch(SQLException e) {
			System.out.println("SQL error when extracting the data from Especie:" + e.getMessage());
           try {
        	   if (rs2 != null) rs2.close();
           } catch (SQLException e1) {
        	   System.out.println("error when closing the ResultSet" + e.getMessage()); 
           }
			
           try {
        	   if (st != null) st.close();
           } catch (SQLException e1) {
        	   System.out.println("error when closing the Statement" + e.getMessage()); 
           }
           
           return null;
           
		} catch(Exception e) {
			System.out.println("error when extracting the data from Especie:" + e.getMessage());
			
			 try {
	        	   if (rs2 != null) rs2.close();
	           } catch (SQLException e1) {
	        	   System.out.println("error when closing the ResultSet" + e.getMessage()); 
	           }
				
	           try {
	        	   if (st != null) st.close();
	           } catch (SQLException e1) {
	        	   System.out.println("error when closing the Statement" + e.getMessage()); 
	           }
	           
		    return null;
		}
		return ejemplares;
	
	}
	
	public int coronapokerus(ArrayList<Ejemplar> ejemplares, int dias) {
		connect();
		Statement st = null;
		
		int x=0;
     		try {
			
			for(int i = 1; i <= dias; i++)
			{
				myConn.setAutoCommit(false);
				 System.out.println(i + " day  ");
				if(i == 1 || i == 2)
				x = 1;
				int k=0;
				for (int j = 1; j <= x; j++)
				{
					
				    Ejemplar ej = Ejemplar.ejemplarRandom(ejemplares);
				   
				    int n_pokedex = ej.getN_pokedex();
				    int n_encuentro = ej.getN_encuentro();
				    
				   if(ej.getInfectado() == 0) {
				    st = myConn.createStatement();
				    String query = "update ejemplar set infectado = 1 where n_pokedex = " + n_pokedex + " and n_encuentro = " + n_encuentro + " ;";
                    ej.setInfectado(1);				   
				    int rs=st.executeUpdate(query);
				     k++;
				    }
				   System.out.println( n_pokedex + "  " + n_encuentro + "   ");
				  
				}
				 x+=k;
				    
				    myConn.commit();
				
			}
		} catch (SQLException e) {
			System.out.println("SQLError: " + e.getMessage());
			System.out.println("Rolling back data here....");
			  try{
				 if(myConn!=null)
		            myConn.rollback();
		      }catch(SQLException se2){
		         se2.printStackTrace();
		      }
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Rolling back data here....");
			  try{
				 if(myConn!=null)
		            myConn.rollback();
		      }catch(SQLException se2){
		         se2.printStackTrace();
		      }
			
		}
     		
     		
				try {
					if(st != null) st.close();
				} catch (SQLException e) {
					 System.out.println("error when closing the Statement" + e.getMessage()); 
				}
     		return x;
	}
	
	
	
		
	public boolean getSprite(int n_pokedex, String filename) {
		connect();
		Statement st = null;
		ResultSet rs= null;
		FileOutputStream fos = null;
		File file = null;
		try{
			st = myConn.createStatement();
			String query = "select sprite from especie where n_pokedex = " + n_pokedex + " ;";
			rs = st.executeQuery(query);
			file = new File(filename);
			fos = new FileOutputStream(file);
			
			byte data[] = null;
			Blob myBlob = null;
			
			while (rs.next()) {
				myBlob = rs.getBlob("sprite");
				data = myBlob.getBytes(1, (int)myBlob.length());	
			}
			
			if(st != null ) st.close();
			if(fos != null ) fos.close();
			if(rs != null) rs.close();
			return true;
			
		} catch(Exception e)
		{
			System.out.println("Error: " + e.getMessage());
			
				try {
					if(st != null ) st.close();
				} catch (SQLException e1) {
					 System.out.println("error when closing the Statement" + e.getMessage()); 
				}
		
				try {
					if(fos != null ) fos.close();
				} catch (IOException e1) {
					 System.out.println("error when closing the File" + e.getMessage()); 
				}
			
				try {
					if(rs != null) rs.close();
				} catch (SQLException e1) {
					 System.out.println("error when closing the ResultSet" + e.getMessage()); 
				}
			return false;
		}
   }
}

//nu uita sa stergi in main, ejemplar si sa modifici in pokemondatabase
