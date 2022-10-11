package Vibee.Repo;

import Vibee.Entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ProductRepo extends JpaSpecificationExecutor<Product>,JpaRepository<Product, Integer>{
	public List<Product> findByStatus(int status);
	@Query("SELECT COUNT(p) from Product p WHERE p.id = :productId")
	long findbyid(@Param("productId") int id);
	
	@Modifying
	@Query("UPDATE Product p SET p.productName= :productName, p.img= :img, p.productType= :type WHERE p.id= :productId")
	int update(@Param("productName") String productName,@Param("img") String img,@Param("type") int type,@Param("productId") int productId);
	
	@Modifying
	@Query("UPDATE Product p SET p.status = 4 WHERE p.id= :productId")
	int delete(@Param("productId") int productId);

	@Query("SELECT p FROM Product p ORDER BY :sortBy ASC")
	List<Product> findAllByFilter(@Param("sortBy") String sortBy, Pageable pageable);

	@Modifying
	@Query("UPDATE Product p SET p.status = 3 WHERE p.id= :productId")
	int lock(@Param("productId") int productId);

	@Modifying
	@Query("UPDATE Product p SET p.status = 1 WHERE p.id= :productId")
	int unLock(@Param("productId") int productId);

	List<Product> findByproductNameStartingWith(String productName, org.springframework.data.domain.Pageable pageable);

	@Query("SELECT count(p) FROM Product p WHERE p.productName like :productName%")
	long countProduct(String productName);

	//usually type = -1 and price = 0
	@Query("select p from Product p where p.status = ?1 and p.productName like ?2")
	public List<Product> findAllProduct(int status, String productNameuct, Pageable pageable);
	
	//usually type != -1 and price = 0
	@Query("select d from Product d where "
			+ "d.productName like ?1 and d.status = ?2 and d.productType = ?3 ")
	public List<Product> findAllProductByType(String productNameuct, int status, int idType,Pageable pageable);
	
	//usually type != -1 and price != 0
	@Query("select d from Product d where "
			+ "d.productName like ?1 and d.status = ?2 and d.productType = ?3  ")
	public List<Product> findAllProductByPriceAndType(String productNameuct, int status, int idType, Pageable pageable);
	
	//usually type = -1 and price != 0
		@Query("select d from Product d where "
				+ "d.productName like ?1 and d.status = ?2 ")
		public List<Product> findAllProductByPrice(String productNameuct, int status, Pageable pageable);
	
	//price > 500.000 and type != -1
	@Query("select d from Product d where "
			+ "d.productName like ?1 and d.status = ?2 and d.productType = ?3 ")
	public List<Product> findAllProductByPriceMore500000AndType(String productNameuct, int status, int idType, Pageable pageable);
	
	//price > 500.000 and type = -1
	@Query("select d from Product d where "
			+ "d.productName like ?1 and d.status = ?2 ")
	public List<Product> findAllProductByPriceMore500000(String productNameuct, int status, Pageable pageable);
	

	@Query("SELECT count(p) FROM Product p WHERE p.barCode like :barCode")
	long countProductByBarCode(String barCode);

//	@Query(value="SELECT p.NAME_PRODUCT as name, p.ID as id, p.createdDate_DATE as createdDateDate, p.IMG as image, t.NAME as categoryName, p.STATUS as statusCode, p.BAR_CODE as barCode, p.DESCRIPTION as description, u.FULLNAME as creator, dp.createdDate_DATE as importDate FROM product p JOIN type_product t ON t.id = p.TYPE_PRODUCT JOIN user u ON u.id=p.CREATOR JOIN detail_product dp ON dp.ID_PRODUCT = p.ID WHERE p.ID = ?1 ORDER BY dp.createdDate_DATE DESC LIMIT 1",nativeQuery = true)
//	Object findProductById(int id);

	@Query("SELECT SUM(p.status) FROM Product p WHERE p.status = 3")
	public int sumReportBlockProduct();

	@Query("SELECT SUM(p.status) FROM Product p WHERE p.status = 2")
	public int sumReportSoldOutProduct();

	public List<Product> findTop6ByOrderByCreatedDateDesc();

	@Query(value = "SELECT product.id,product.created_date,user.fullname,product.status,product.bar_code,product.description,type_product.name, sum(warehouse.in_amount) as inAmount, sum(export.out_amount/unit.amount) as outAmount,product.img,product.name_product\n" +
			"from product join type_product on type_product.id=product.type_product \n" +
			"join warehouse on warehouse.id_product=product.id join export on export.id_warehouse=warehouse.id join unit on unit.id=export.id_unit join user on user.id=product.creator where product.id= ?1 group by product.id", nativeQuery = true)
	Object findProductById( int id);

	@Query(value = "SELECT product.status,count(warehouse.id), sum(warehouse.in_amount) as inAmount, sum(export.out_amount/unit.amount) as outAmount,product.img,product.name_product, unit.unit_name,sum(warehouse.in_money), sum(export.out_price*export.out_amount)\n" +
			"\t\t\tfrom product\n" +
			"\t\t\tjoin warehouse on warehouse.id_product=product.id join export on export.id_warehouse=warehouse.id join unit on unit.id=export.id_unit where product.id= ?1 and unit.parent_id=0 group by product.id",nativeQuery = true)
	Object getBasicProductById( int id);

	@Query("select p.id,p.productName,p.status,p.img, (sum(w.inAmount)-sum(e.outAmount/u.amount))as inventory,e.outPrice,u.unitName,s.nameSup  from Product p " +
			"join Warehouse w on p.id=w.productId " +
			"join Export e on e.warehouseId=w.id " +
			"join Unit u on u.id=e.unitId join Supplier s on s.id=w.supplierId where p.productName like :productName% " +
			"group by p.id ")
	List<Object> getProducts(@Param("productName") String search, Pageable pageable);

	@Query(value = "SELECT count(product.id)\n" +
			"from product join warehouse on product.id = warehouse.id_product join export on export.id_warehouse=warehouse.id\n" +
			" join unit on unit.id=export.id_unit join type_product t on t.id = product.type_product\n" +
			" where product.type_product= ?1\n" +
			" having (sum(warehouse.in_amount) - sum(export.out_amount/unit.amount)) >0" , nativeQuery = true)
	String amountProductByType(int idType);

	@Query(value = "SELECT count(product.id)\n" +
			"from product join warehouse on product.id = warehouse.id_product join export on export.id_warehouse=warehouse.id\n" +
			"join unit on unit.id=export.id_unit join type_product t on t.id = product.type_product\n" +
			"where t.parent_id= ?1 or product.type_product =?1\n" +
			"having (sum(warehouse.in_amount) - sum(export.out_amount/unit.amount)) >0 " , nativeQuery = true)
	String amountProductByType1(int idParent);

    @Query("SELECT (sum(w.inAmount)-sum(e.outAmount/u.amount))as inventory " +
			"from Product p join Warehouse w on p.id = w.productId join Export e on e.warehouseId=w.id join Unit u on u.id=e.unitId\n" +
			"where w.productId= ?1" +
			"group by  p.id")
	Long amount(int idProduct);

}
