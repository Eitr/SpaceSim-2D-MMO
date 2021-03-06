package net.eitr.gin.server;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import net.eitr.gin.Units;
import net.eitr.gin.Units.WorldBodyType;
import net.eitr.gin.network.GraphicsData;
import net.eitr.gin.network.PolygonData;

public class Rock extends WorldBody {

	private float size;
	private Body body;
	private float [] vertices;
	private Color color;

	public Rock (Body b) {
		super(WorldBodyType.ROCK);
		
		body = b;
		body.setUserData(this);
		size = MathUtils.random(6, 24);
		
		//		float [] vertices = generateConcavePolygon(size);
		vertices = generateCircularPolygon(size);

		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		FixtureDef fDef = new FixtureDef();
		fDef.shape = shape;
		fDef.density = 10.0f;
		fDef.friction = 0.4f;
		fDef.restitution = 0.1f;
		body.createFixture(fDef);
		shape.dispose();
		
		color = new Color(MathUtils.random(0f,0.1f)+.6f,MathUtils.random(0f,0.1f)+.6f,MathUtils.random(0f,0.1f)+.6f,1);
	}

	/** Create triangle regions that make up the polygon for rendering */
	public static short[] getTriangles (float[] v) {
		short[] points = new short[(v.length-4)/2*3];
		for (int i=0; i < points.length/3; i++) {
			points[i*3] = 0;
			points[i*3+1] = (short)(i+1);
			points[i*3+2] = (short)((i+2));
		}
		return points;
	}

	@SuppressWarnings("unused")
	private float[] generateConcavePolygon (float r) {
		int p = 8; // total points around polygon
		float [] vertices = new float[p*2];
		float radians = MathUtils.PI*2f/p; // angle between vertices

		for (int i=0; i<p; i++) {
			float dist = MathUtils.random(r/2,r);
			vertices[i*2] = MathUtils.cos(i*radians)*dist;
			vertices[i*2+1] = MathUtils.sin(i*radians)*dist;
		}
		return vertices;
	}

	private float[] generateCircularPolygon (float r) {
		int p = 8; // total points around polygon (LIBGDX LIMIT 8 VERTICES PER POLYGON)
		float [] vertices = new float[p*2];
		float radians = MathUtils.PI*2f/p; // angle between vertices

		for (int i=0; i < p; i++) {
			//TODO asteroid shaping
//			float angle = MathUtils.random(0f,radians);
			float angle = MathUtils.random(radians/2,radians);
			vertices[i*2] = MathUtils.cos(angle+i*radians)*r;
			vertices[i*2+1] = MathUtils.sin(angle+i*radians)*r;
		}
		return vertices;
	}

	void getGraphics (GraphicsData g) {
		if (Vector2.dst(g.x, g.y, body.getPosition().x, body.getPosition().y) > Units.MAX_VIEW_DIST) { 
			return;
		}
		PolygonData poly = new PolygonData(body.getPosition().x, body.getPosition().y, body.getAngle(), vertices);
		poly.setColor(color.r,color.g,color.b,color.a);
		g.rocks.add(poly);
	}
}
