MscViewer for Nokia MSC/MSS
===
Manage Nokia MSC/MSS configurations through a modern web UI with AI-assisted analysis and automation.                                   

![screenshot 1](./docs/assets/p3x.png)

🚀 [Live Demo](https://demo.greenstones.de/msc-viewer)

## Features

* **Configuration visualization** — browse hierarchy, relationships, and dependency graphs.
* **Map view** — see configurations in geographical context using coordinates and coverage data.
* **External data enrichment** — integrate labels, names, and coordinates from external systems.
* **AI agent** — query and manage configurations using natural language, including automated MSC/MSS command generation.
* **Detail views & export** — inspect individual objects and export data to CSV/Excel.
* **Multi-instance management** — manage multiple MSC/MSS nodes through a single interface.
* **Customizable** — declaratively define configurations and relationships, tailored to specific requirements such as GSM-R.

## Overiew

Since Nokia MSS/MSCs vary in versions and configurations, a one-size-fits-all UI is not practical. MscViewer is a toolkit for creating efficient and high-quality web applications for MSC/MSS, tailored to customer requirements — including an AI agent that adapts to the specific data and workflows.

MscViewer connects to the MSC via terminal, replacing manual command input with a web interface that offers high-level functions like configuration linking, object graph and map visualizations — and an AI agent for natural language queries, automated analysis, and intelligent navigation of complex configurations.

![screenshot 2](./docs/assets/p5.png)

The integrated AI agent lets you interact with MSC configurations using plain language. Ask it to find cells, location areas, GCAs, or GCREFs — it queries the underlying graph database and returns results as navigable links. When you need to make changes, describe what you want in natural language and the agent generates the corresponding MSC/MSS commands (e.g. `ZEPC`, `ZHAC`, `ZHGC`) ready to execute. It understands configuration relationships, validates referenced objects before generating commands, and asks for clarification when parameters are missing.

![screenshot 3](./docs/assets/p6.png)


The tool helps visualize configurations on a map to provide location context. Providing location information, such as coordinates or shapes, for the base objects allows the tool to calculate geographical representations of related configurations. For example, with latitude and longitude coordinates for cells and their coverage data, MscViewer can calculate shapes for location areas, GCREFs, and GCAs and put them on the map.




## 🚀 Live Demo

Check out the live demo here: [https://demo.greenstones.de/msc-viewer](https://demo.greenstones.de/msc-viewer)


## Try using Docker Compose

Run the application with sample data (without an MSC/MSS):

```sh
# build and run the entire stack
docker compose up --build

# open the web app
open http://localhost:9999/msc-viewer

# username/password: admin/admin1a!
```
 
## Getting Started

To compile and run the application:

```sh
# copy the environment template and add your AI provider key
cp .env.template .env
# edit .env and set your key, e.g. OPENAI_API_KEY=sk-...

# start neo4j 
docker compose up -d  msc-neo4j

# run the backend subproject
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=simple


# run the frontend subproject
cd frontend
npm install
npm run dev

# open the web app
open http://localhost:3000/msc-viewer

# username/password: admin/admin1a!
```

### Customizing the application
With MSC Viewer, you can declaratively define the MSC configurations you want to manage and their relationships. The GUI and dependency graph are then generated based on these configurations. Below is an example configuration for BTS configurations (Command `ZEPO`) : 

```java
	public ConfigType cells() {
		return new ConfigTypeBuilder()
				.listCommand("ZEPO::IDE") // MSC command to load the list of cells
				.detailCommand("ZEPO:NO=${NO}") // MSC command to load the cell with the given number
				.parser(new ConfigTypeParserBuilder() // Parser for the list of cells
						.listSeparator("BASE TRANSCEIVER STATION") // Separator for the list of cells
						.build()) //
				.defaultId("NO=BTS|NUMBER") //
				.frontend(f -> f //
						.title("Cells and BTSs") // list page title
						.column("BTS|NUMBER") // table columns
						.column("BTS|NAME", col -> col.linkToDetail()) //

						.detail(d -> d //
								.title("${NAME}", "NAME=BTS|NAME") // detail page title template
								.props("BTS|NAME", "BTS|NUMBER")// properties to display
								.propSeparator()//
								.prop("LA|LAC")//
							

						))
				.build();

	}
```


### Connect to MSC Instances

To connect to MSC instances, configure the necessary connection details such as the host, port and user credentials in the `backend/src/main/resources/application.yml`. Below is an example configuration for two MSC instances:

```yaml
instances:
  - id: MSS-INTG-01
    host: host-intg
    port: 22
    user: user
    password: pwd
  - id: MSS-PROD-01
    host: host-prod
    port: 22
    user: user
    password: pwd
```


## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Open Source & Commercial Support  

MscViewer is 100% open-source (MIT License) and free to use. For organizations that need additional security, scalability, or expert assistance, we offer professional services and enterprise support:  

✔ Priority Support: SLA-backed response times  
✔ Custom Development: Tailored integrations and enhancements  
✔ Consulting: Architecture reviews, performance tuning  
✔ Long-Term Maintenance: Security patches, version upgrades  

Visit our [product page](https://www.greenstones.de/solutions/msc-viewer) to learn more about how MSC Viewer can transform your Nokia MSC/MSS management.

[**Contact Our Team**](mailto:info@greenstones.de)  


## Contributing
Contributions are welcome! Open an issue or submit a pull request to suggest changes.

## 👥 About Us

We're [Greenstones GmbH](https://www.greenstones.de) — we build custom enterprise apps with a focus on GSM-R, Location Intelligence, and Geocoding.
