package uniandes.centalsimulator.reader;

import java.io.FileWriter;

public class Writer  {

	public void Write(String line, String filename){

		FileWriter file = null;

		try
		{
			file= new FileWriter(filename,true);
			file.write(line);
			file.write("\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Nuevamente aprovechamos el finally para 
				// asegurarnos que se cierra el fichero.
				if (null != file)
					file.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

}
