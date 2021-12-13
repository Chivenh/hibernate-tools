/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.hbm2x.JdbcHbm2JavaEjb3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.persistence.Persistence;

/**
 * @author max
 * @author koen
 *
 */
//TODO HBX-2261: Investigate, fix and reenable failing tests after update to 6.0.0.Beta2		
@Disabled
public class TestCase {
	
	@TempDir
	public File outputDir = new File("output");
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		Exporter exporter = ExporterFactory.createExporter(ExporterType.JAVA);
		exporter.getProperties().put(
				ExporterConstants.METADATA_DESCRIPTOR, 
				MetadataDescriptorFactory.createReverseEngineeringDescriptor(null, null));
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.getProperties().put(ExporterConstants.TEMPLATE_PATH, new String[0]);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	//TODO HBX-2261: Investigate, fix and reenable failing tests after update to 6.0.0.Beta2		
	@Disabled
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile( new File(outputDir.getAbsolutePath() + "/Master.java") );
	}

	//TODO HBX-2261: Investigate, fix and reenable failing tests after update to 6.0.0.Beta2		
	@Disabled
	@Test
	public void testUniqueConstraints() {
		assertEquals(null, FileUtil.findFirstString( "uniqueConstraints", new File(outputDir,"Master.java") ));
		assertNotNull(FileUtil.findFirstString( "uniqueConstraints", new File(outputDir,"Uniquemaster.java") ));
	}
	
	//TODO HBX-2261: Investigate, fix and reenable failing tests after update to 6.0.0.Beta2		
	@Disabled
	@Test
	public void testCompile() {
		File destination = new File(outputDir, "destination");
		destination.mkdir();
		List<String> jars = new ArrayList<String>();
		jars.add(JavaUtil.resolvePathToJarFileFor(Persistence.class)); // for jpa api
		JavaUtil.compile(outputDir, destination, jars);
		JUnitUtil.assertIsNonEmptyFile(new File(destination, "Master.class"));
		JUnitUtil.assertIsNonEmptyFile(new File(destination, "Uniquemaster.class"));
	}
	
}
