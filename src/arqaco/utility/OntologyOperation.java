package arqaco.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import stocker.heuristic.HeuristicsRegistry;
import stocker.probability.IndexLevel;
import stocker.probability.Probability;
import stocker.probability.ProbabilityFactory;
import stocker.probability.impl.ProbabilityIndex;
import stocker.util.Config;
import stocker.util.Constants;
import arqaco.ds.QueryInfo;
import arqaco.ds.QueryResult;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryCancelledException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.Transform;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * 
 * @author E. Guzel Kalayci
 */
public class OntologyOperation {

	private static OntModel model;

	private final static long TIMEOUT = 7200000;

	public static long size = -1;

	public static Config config;
	public static Model indexModel;
	public static Probability probability;

	public static void initOntology() {
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
	}

	public static String loadOntology(String inputFileName) throws Exception {
		if (model == null) {
			initOntology();
		}
		String loadedFile = inputFileName;
		ZipFile zf = null;

		try {

			OntModel subOntModel = ModelFactory.createOntologyModel(
					OntModelSpec.OWL_MEM, null);

			InputStream in;
			if (inputFileName.contains(".zip")) {
				zf = new ZipFile(inputFileName);
				ZipEntry entry = zf.entries().nextElement();
				loadedFile = entry.toString();
				in = zf.getInputStream(entry);
			} else {
				in = FileManager.get().open(inputFileName);
				if (in == null) {
					throw new IllegalArgumentException("File: " + inputFileName
							+ " not found");
				}
			}

			// read the RDF/XML file
			subOntModel.read(in, "");
			model.addSubModel(subOntModel);

		} catch (JenaException jx) {
			if (jx.getCause() instanceof NoRouteToHostException
					|| jx.getCause() instanceof UnknownHostException
					|| jx.getCause() instanceof ConnectException
					|| jx.getCause() instanceof IOException) {
				SWANTLogger
						.severe("Cannot access public internet - content negotiation test not executed");
			} else {
				SWANTLogger.severe(jx.getMessage());
				throw jx;
			}
		} catch (Exception e) {
			SWANTLogger.info(e.getMessage());
		}
		// update size of ontology
		size = model.size();
		if (zf != null)
			zf.close();
		return loadedFile;
	}

	public static void listingProperties() {
		ExtendedIterator<DatatypeProperty> iter = model
				.listDatatypeProperties();
		DatatypeProperty dataPro;
		int i = 0;
		while (iter.hasNext()) {
			i++;
			dataPro = iter.next();
			SWANTLogger.info(i + " Data Type Property : "
					+ dataPro.getLocalName());
		}
	}

	public static void listingIndividuals() {
		ExtendedIterator<Individual> iter = model.listIndividuals();
		Individual ind;
		int i = 0;
		while (iter.hasNext()) {
			i++;
			ind = iter.next();
			SWANTLogger.info(i + " Individual : " + ind.getLocalName());
		}

	}

	public static void listingClasses() {

		ExtendedIterator<OntClass> iter = model.listClasses();
		OntClass ontClass;
		int i = 0;
		while (iter.hasNext()) {
			i++;
			ontClass = iter.next();
			SWANTLogger.info(i + " Ont Class : " + ontClass.getLocalName());
		}
	}

	public static QueryResult executeQuery(String query) {
		long start = System.currentTimeMillis();
		QueryResult qr = executeQuery(QueryFactory.create(query));
		qr.type = "NOR";
		SWANTLogger.info("TOTAL EXECUTE TIME:"
				+ (System.currentTimeMillis() - start) + " ms RESULT COUNT:"
				+ qr.resultCount);
		return qr;
	}

	public static QueryResult executeQuery(String query, Transform transform) {
		if (query.contains("DISTINCT"))
			return executeDistinctQuery(query, transform);
		return executePlainQuery(query, transform);
	}

	/**
	 * duz sorgu calistirma
	 * 
	 * @param query
	 * @param transform
	 * @return
	 */
	private static QueryResult executePlainQuery(String query,
			Transform transform) {
		SWANTLogger.fine("PLAIN OPT QUERY");
		// Transform (optimize) query
		long start = System.currentTimeMillis();
		Query _query = QueryFactory.create(query);
		Op op = getAlgebra(_query);
		OpProject opp = (OpProject) op;
		SWANTLogger.info("--ALGEBRA TIME:"
				+ (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		OpProject newopp = (OpProject) Transformer.transform(transform, opp);
		SWANTLogger.info("--TRANSFORM TIME:"
				+ (System.currentTimeMillis() - start));

		// Execute query
		start = System.currentTimeMillis();
		_query = OpAsQuery.asQuery(newopp);
		QueryResult qr = executeQuery(_query);
		qr.type = "OPT";
		SWANTLogger.info("TOTAL EXECUTE TIME:"
				+ (System.currentTimeMillis() - start) + " RESULT COUNT:"
				+ qr.resultCount);
		// return result
		return qr;
	}

	/**
	 * distinct iceren sorgu calistirma
	 * 
	 * @param query
	 * @param transform
	 * @return
	 */
	private static QueryResult executeDistinctQuery(String query,
			Transform transform) {
		SWANTLogger.fine("DISTINCT OPT QUERY");

		// Transform (optimize) query
		long start = System.currentTimeMillis();
		Query _query = QueryFactory.create(query);
		Op op = getAlgebra(_query);
		OpProject opp = (OpProject) ((OpDistinct) op).getSubOp();
		SWANTLogger.info("--ALGEBRA TIME:"
				+ (System.currentTimeMillis() - start));
		OpProject newopp = (OpProject) Transformer.transform(transform, opp);
		Op newdop = new OpDistinct(newopp);
		SWANTLogger.info("--TRANSFORM TIME:"
				+ (System.currentTimeMillis() - start));

		// Execute query
		start = System.currentTimeMillis();
		_query = OpAsQuery.asQuery(newdop);
		QueryResult qr = executeQuery(_query);
		qr.type = "OPT";
		SWANTLogger.info("TOTAL EXECUTE TIME:"
				+ (System.currentTimeMillis() - start) + " RESULT COUNT:"
				+ qr.resultCount);
		// return result
		return qr;
	}

	private static QueryResult executeQuery(Query _query) {
		SWANTLogger.info("QUERY\n" + _query);
		// SWANTLogger.fine("ALGEBRA\n"+getAlgebraAsString(_query));
		long start = System.nanoTime();
		QueryResult qr = new QueryResult();
		QueryExecution qe = QueryExecutionFactory.create(_query, model);
		// qe.getContext().set(ARQ.symLogExec, Explain.InfoLevel.ALL) ;
		qe.setTimeout(TIMEOUT);
		// QueryExecutionBase
		// StageGeneratorGeneric
		ResultSet resultSet = qe.execSelect();
		// arq.qparse.main(new String[]{"--explain",_query.toString()});
		// ResultSetStream
		SWANTLogger.fine("--EXEC TIME:"
				+ ((float) (System.nanoTime() - start) / 1000000) + " ms");
		start = System.currentTimeMillis();
		ArrayList<String> results = new ArrayList<String>();
		try {
			while (resultSet.hasNext()) {
				QuerySolution next = resultSet.next();
				results.add(next.toString());
			}
			// Important - free up resources used running the query
			qe.close();
		} catch (QueryCancelledException e) {
			SWANTLogger.severe("Query Cancelled.");
			qr.resultCount = 0;
			qr.results = null;
			return qr;
		}
		qr.results = results;
		qr.query = _query.toString();
		qr.resultCount = results.size();
		SWANTLogger.fine("--POPULATE TIME:"
				+ (System.currentTimeMillis() - start) + " ms");
		return qr;
	}

	/**
	 * bir ust duzey destekleyen sorgu calistirma
	 * 
	 * @param query
	 * @param transform
	 * @return
	 */
	public static QueryResult _executeQuery(String query, Transform transform) {
		Query _query = QueryFactory.create(query);
		long start = System.currentTimeMillis();

		Op op = getAlgebra(_query);
		OpProject opp = getProjectOp(op);

		SWANTLogger
				.info("ALGEBRA TIME:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		SWANTLogger.info("GETBGP TIME:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		OpProject newopp = (OpProject) Transformer.transform(transform, opp);
		Op newop = createOp(op, newopp);
		SWANTLogger.info("APPLY TRANSFORM TIME (OO):"
				+ (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		_query = OpAsQuery.asQuery(newop);
		QueryResult qr = executeQuery(_query);
		qr.type = "OPT";
		SWANTLogger.info("EXEC TIME:" + (System.currentTimeMillis() - start)
				+ " RESULT COUNT:" + qr.resultCount);
		return qr;
	}

	private static Op createOp(Op op, OpProject newopp) {
		Op newop = newopp;
		if (!(op instanceof OpProject)) {
			try {
				Constructor<? extends Op> constructor = op.getClass()
						.getConstructor(Op.class);
				newop = constructor.newInstance(newopp);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return newop;
	}

	private static OpProject getProjectOp(Op op) {
		if (op instanceof OpDistinct) {
			return (OpProject) ((OpDistinct) op).getSubOp();
		}
		return (OpProject) op;
	}

	/**
	 * PF icin gereken index modelini LightWeight olarak uretir
	 * 
	 * @return
	 */
	public static Model generateLightWeightIndex() {
		return generateIndex(IndexLevel.LIGHTWEIGHT);
	}

	/**
	 * PF icin gereken index modelini Full olarak uretir
	 * 
	 * @return
	 */
	public static Model generateFullIndex(int indexLevel) {
		return generateIndex(indexLevel);
	}

	private static Model generateIndex(int indexLevel) {
		long startTime = System.currentTimeMillis();
		ProbabilityIndex pi = new ProbabilityIndex();
		Config c = new Config();
		c.setIndexLevel(indexLevel);
		pi.create(model, c);
		SWANTLogger.severen("Index Generated:"
				+ (System.currentTimeMillis() - startTime));
		return pi.getModel();
	}

	public static Model loadIndex(String filename) {
		OntModel subOntModel = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM, null);

		InputStream in = FileManager.get().open(filename);
		if (in == null) {
			throw new IllegalArgumentException("File: " + filename
					+ " not found");
		}

		// read the RDF/XML file
		subOntModel.read(in, "");
		return subOntModel;
	}

	public static void prepareForPFJ(String indexFilename) {
		long startTime = System.currentTimeMillis();
		indexModel = OntologyOperation.loadIndex(indexFilename);
		config = new Config();
		config.setBasicPatternHeuristic(HeuristicsRegistry.BGP_PROBABILISTIC_FRAMEWORK_JOIN);
		probability = ProbabilityFactory.loadDefaultModel(
				OntologyOperation.getModel(), OntologyOperation.indexModel,
				OntologyOperation.config);
		ARQ.getContext().set(Constants.PF, probability);
		SWANTLogger.severen("Probability model loaded:"
				+ (System.currentTimeMillis() - startTime));
	}

	/**
	 * PF icin gereken index modeli
	 * 
	 * @return
	 */
	public static void saveFullIndex(String filename) {
		Model m = generateFullIndex(IndexLevel.FULL);
		try {
			m.write(new FileOutputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * PF icin gereken index modeli
	 * 
	 * @return
	 */
	public static void saveLightWeightIndex(String filename) {
		Model m = generateFullIndex(IndexLevel.LIGHTWEIGHT);
		try {
			m.write(new FileOutputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static OntModel getModel() {
		return model;
	}

	public static Model getIndex() {
		return indexModel;
	}

	public static void setModel(OntModel ontModel) {
		OntologyOperation.model = ontModel;
	}

	public static String getAlgebraAsString(String query) {
		return getAlgebra(QueryFactory.create(query)).toString();
	}

	public static String getAlgebraAsString(Query query) {
		return getAlgebra(query).toString();
	}

	private static Op getAlgebra(Query query) {
		return Algebra.compile(query);
	}

	@SuppressWarnings("unused")
	private static OpBGP getBGP(OpProject op) {
		return (OpBGP) op.getSubOp();
	}

	protected static ArrayList<String> execAlgebraQuery(Op op) {

		// Sparql Algebra kullanarak sorgu calistirma
		// System.out.println("OP:" + op);

		op = Algebra.optimize(op);
		Query query = OpAsQuery.asQuery(op);

		QueryExecution qe = QueryExecutionFactory.create(query, model);
		// ARQ.setExecutionLogging(Explain.InfoLevel.ALL) ;
		// qe.getContext().set(ARQ.symLogExec, Explain.InfoLevel.ALL) ;
		ResultSet resultSet = qe.execSelect();
		ArrayList<String> result = new ArrayList<String>();

		while (resultSet.hasNext()) {
			result.add(resultSet.next().toString());
		}
		// Important - free up resources used running the query
		qe.close();

		return result;
	}

	public static QueryInfo loadQueryFromFile(File file) {
		return loadQueryFromFile(file.getAbsolutePath());
	}

	/**
	 * bu metot qr uzantili bizim ozel dosyalarimiz icin dosyalarin ilk satiri
	 * tamsayi olarak sorgu sonuc sayisini tutuyor sonraki satirlar sona kadar
	 * sorguyu tutuyor
	 * 
	 * @param file
	 *            dosya ismi
	 * @return sorgu
	 */
	public static QueryInfo loadQueryFromFile(String file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			QueryInfo qi = new QueryInfo();
			String strLine = in.readLine();
			// TODO dosya basinda yorum olacak mi?
			// while(strLine.startsWith("#"))
			// strLine = in.readLine();
			qi.resultCount = Integer.parseInt(strLine);
			String query = "";
			// Read File Line By Line
			while ((strLine = in.readLine()) != null) {
				// if(strLine!="" && !strLine.startsWith("#"))
				query += strLine + "\n";
			}
			qi.query = query;
			in.close();
			return qi;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveQueryToFile(String file, QueryInfo qi) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(qi.resultCount + "\n" + qi.query);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Context getExecutionContext() {
		return ARQ.getContext();
	}

	public static Graph getGraph() {
		return model.getGraph();
	}

	public static void main(String[] args) throws Exception {
		OntologyOperation.loadOntology("data/univ-bench.owl");
		OntologyOperation.loadOntology("data/university_v2.zip");
		OntologyOperation
				.saveLightWeightIndex("data/university_v2_LW_index.owl");
	}
}
