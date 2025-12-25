package dev.quinteger.aoc.util

import java.util.ArrayDeque
import java.util.Queue
import java.util.SequencedSet

//@JvmOverloads
//fun <N> bfsFindShortestPath(startNode: N, neighbourExtractor: (N) -> Collection<N>, endNode: N, logOutput: Boolean = false): List<N> {
//    return bfsFindShortestPath(startNode, neighbourExtractor, endNode!!::equals, logOutput)
//}

@JvmOverloads
fun <N> bfsFindShortestPath(
    startNode: N,
    neighbourExtractor: (N) -> Collection<N>,
    endPredicate: (N) -> Boolean,
    pathCostFunction: (List<N>) -> Long = { it.size.toLong() },
    logOutput: Boolean = false
): List<N> {
    val nodesToCheck: Queue<N> = ArrayDeque()
    val checkedNodesWithPaths: MutableMap<N, NodeInfo<N>> = HashMap()
    nodesToCheck.add(startNode)
    val path = listOf(startNode)
    val cost = pathCostFunction(path)
    checkedNodesWithPaths[startNode] = NodeInfo(path, cost)
    var checkedNodes = 0
    var pathLength = 1
    var pathCost = Long.MAX_VALUE
    var foundPath: List<N>? = null
    while (!nodesToCheck.isEmpty()) {
        val nodeToCheck = nodesToCheck.remove()
        val nodeInfo = checkedNodesWithPaths[nodeToCheck]!!
        val pathToNode = nodeInfo.path
        val pathToNodeSize = pathToNode.size
        if (logOutput && pathToNodeSize > pathLength) {
            println("Checked $checkedNodes nodes with path length $pathLength")
            checkedNodes = 0;
            pathLength = pathToNodeSize;
        }
        checkedNodes++
        if (endPredicate(nodeToCheck)) {
            val newPathCost = pathCostFunction(pathToNode)
            if (logOutput) {
                println("Found end node $nodeToCheck with path cost $newPathCost")
            }
            if (newPathCost < pathCost) {
                pathCost = newPathCost
                foundPath = pathToNode
            }
        }
        val nextNodes = neighbourExtractor(nodeToCheck)
        for (nextNode in nextNodes) {
            val existingNodeInfo = checkedNodesWithPaths[nextNode]
            val nextNodePath = buildList(pathToNodeSize + 1) {
                addAll(pathToNode)
                add(nextNode)
            }
            val nextNodePathCost = pathCostFunction(nextNodePath)
            if (existingNodeInfo == null || nextNodePathCost < existingNodeInfo.pathCost) {
                nodesToCheck.add(nextNode)
                checkedNodesWithPaths[nextNode] = NodeInfo(nextNodePath, nextNodePathCost)
            }
        }
    }
    return foundPath!!
}

private data class NodeInfo<N>(val path: List<N>, val pathCost: Long)

fun <N> bfsCountAllDistinctPaths(
    startNode: N,
    neighbourExtractor: (N) -> Collection<N>,
    endNode: N,
): Long {
    val nodesToCheck: Queue<N> = ArrayDeque()
    val checkedNodesWithPaths: MutableMap<N, Long> = HashMap()
    nodesToCheck.add(startNode)
    checkedNodesWithPaths[startNode] = 1L
    while (!nodesToCheck.isEmpty()) {
        val nodeToCheck = nodesToCheck.remove()
        val pathsToNode = checkedNodesWithPaths[nodeToCheck]!!
        val nextNodes = neighbourExtractor(nodeToCheck)
        for (nextNode in nextNodes) {
            if (!checkedNodesWithPaths.containsKey(nextNode)) {
                nodesToCheck.add(nextNode)
            }
            checkedNodesWithPaths.merge(nextNode, pathsToNode, Long::plus)
        }
    }
    return checkedNodesWithPaths[endNode]!!
}

@JvmOverloads
fun <N> dfsFindAllPaths(
    startNode: N,
    neighbourExtractor: (N) -> Collection<N>,
    endNode: N,
    pathPredicate: ((List<N>) -> Boolean)? = null,
): List<List<N>> {
    val foundPaths: MutableList<List<N>> = ArrayList()
    dfsWalkRecursive(
        startNode,
        neighbourExtractor,
        endNode,
        pathPredicate,
        foundPaths,
        ArrayList(),
        LinkedHashSet()
    )
    return foundPaths.toList()
}

private fun <N> dfsWalkRecursive(
    currentNode: N,
    neighbourExtractor: (N) -> Collection<N>,
    endNode: N,
    pathPredicate: ((List<N>) -> Boolean)?,
    foundPaths: MutableList<List<N>>,
    currentPath: MutableList<N>,
    visitedNodes: SequencedSet<N>
) {
    currentPath.add(currentNode)
    visitedNodes.add(currentNode)
    if (currentNode == endNode && if (pathPredicate != null) pathPredicate(currentPath) else true) {
        foundPaths.add(currentPath.toList())
    } else {
        val neighbours = neighbourExtractor(currentNode)
        for (neighbour in neighbours) {
            if (!visitedNodes.contains(neighbour)) {
                dfsWalkRecursive(
                    neighbour,
                    neighbourExtractor,
                    endNode,
                    pathPredicate,
                    foundPaths,
                    currentPath,
                    visitedNodes
                )
            }
        }
    }
    currentPath.removeLast()
    visitedNodes.removeLast()
}

fun <N> bfsFindAllPaths(
    startNode: N,
    neighbourExtractor: (N) -> Collection<N>,
    endNode: N,
) {
    val pathsToCheck: Queue<List<N>> = ArrayDeque()
    pathsToCheck.add(listOf(startNode))
    val foundPaths: MutableList<List<N>> = ArrayList()
    while (!pathsToCheck.isEmpty()) {
        val currentPath = pathsToCheck.remove()
        val lastNode = currentPath.last()
        if (lastNode == endNode) {
            foundPaths.add(currentPath)
        } else {
            val neighbours = neighbourExtractor(lastNode)  
        }
    }
}
