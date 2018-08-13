
#include <map>
#include <vector>
#include <string>
#include <sstream>
#include <iostream>
#include <unistd.h>
using namespace std;


int n;
vector<vector<int> > AdjacencyMatrix;
map<string, int> VertexIndex;
map<int, string> VertexIDs;
int NextVertexID = 0;


void ReadTGFGraph(istream& is, bool Directed = false)
{
    string Temp;
    string Vertex1;
    string Vertex2;

    /// Read vertices
    is.peek(); // update eof info
    while(!is.eof())
    {
        getline(is, Temp);
        if(Temp.length() <= 0)
            continue;
        if(Temp[0] == '#')
            break;

        stringstream strstream(Temp);
        strstream >> Vertex1;

        if(VertexIndex.find(Vertex1) != VertexIndex.end())
            throw "Same vertex ID multiple times";

        int thisVertexID = NextVertexID++;
        VertexIndex[Vertex1] = thisVertexID;
        VertexIDs[thisVertexID] = Vertex1;
        is.peek(); // update eof info
    }

    
    n = NextVertexID;
    AdjacencyMatrix = vector<vector<int> >(n,vector<int>(n, 0));
    
    
    /// Read edges
    is.peek(); // update eof info
    while(!is.eof())
    {
        getline(is, Temp);
        if(Temp.length() <= 0)
            continue;
        if(Temp[0] == '#')
            break;

        stringstream strstream(Temp);
        strstream >> Vertex1 >> Vertex2;

        if(VertexIndex.find(Vertex1) == VertexIndex.end()
            || VertexIndex.find(Vertex2) == VertexIndex.end())
            throw "Edge containing undeclared Vertex";

        AdjacencyMatrix[VertexIndex[Vertex1]][VertexIndex[Vertex2]]++;
        if(!Directed && (Vertex1 != Vertex2))
            AdjacencyMatrix[VertexIndex[Vertex2]][VertexIndex[Vertex1]]++;

        is.peek(); // update eof info
    }

}

int main(int argc, char** argv)
{
    ReadTGFGraph(cin, false);
    
    int cyclelength = 5;
    
    for(int i = 0; i < cyclelength; i++)
    {
        cout << "CREATEVERTEX v" << i << endl;
        cout << "SETVERTEXCOLOR v" << i << " #ff0000" << endl;
        cout << "SETVERTEXLABEL v" << i << " v_" << i << endl;
        cout << "MOVEVERTEX v" << i << " " << i << " " << i << endl;
        usleep(1000000); // 1 second
   }

    for(int i = 0; i < cyclelength; i++)
    {
        int j = (i+1)%cyclelength;
        cout << "CREATEEDGE e" << i << " v" << i << " v" << j << endl;
        cout << "SETEDGECOLOR e" << i << " #00ff00" << endl;
        cout << "SETEDGELABEL e" << i << " e_" << i << endl;
        usleep(1000000); // 1 second
    }
    
    cout << "QUIT" << endl;
    return 0;
}