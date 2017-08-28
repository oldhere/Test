package Pool;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DBConnectionPool {
	private int minConn=1;
	private int maxConn=3;
	
	private String driver="com.mysql.jdbc.Driver";
	private String url="jdbc:mysql://localhost:3306/opc";
	private String user="root";
	private String password="root";
	
	private int connAmount=0;
	private Stack<Connection> connStack=new Stack<Connection>(); //ʹ��stack������
	private static DBConnectionPool instance=null;
	
	//����Ψһʵ��������ǵ�һ�ε��ô˷������򴴽���ʵ��
	public static synchronized DBConnectionPool getInstance(){
		if(instance==null){
			instance=new DBConnectionPool();
		}
		return instance;
	}

	//�����ӳ�����һ���������ӡ����û�п��������ҵ�ǰ������С��������������򴴽��µ�����
	public synchronized Connection getConnection(){
		Connection con=null;
		
		if(!connStack.empty()){
			con=(Connection)connStack.pop();
		}else if(connAmount<maxConn){
			con=newConnection();
		}else{
			
			try {
				wait(10000);
				return getConnection();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return con;
	}
	
	//������ʹ�õ����ӷ��ظ����ӳ�
	public synchronized void freeConnection(Connection con){
		connStack.push(con);
		notifyAll();
	}
	
	//����������
	private Connection newConnection(){
		Connection con=null;
		try {
			con=DriverManager.getConnection(url,user,password);
			connAmount++;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return con;
	}
	
	
	

}
