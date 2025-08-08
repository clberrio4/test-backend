SELECT DISTINCT c.nombre
FROM cliente c
JOIN inscripcion i ON c.id = i.idCliente
JOIN disponibilidad d ON i.idProducto = d.idProducto
WHERE NOT EXISTS (
    SELECT 1
    FROM disponibilidad d2
    WHERE d2.idProducto = i.idProducto
      AND d2.idSucursal NOT IN (
          SELECT v.idSucursal
          FROM visitan v
          WHERE v.idCliente = c.id
      )
);