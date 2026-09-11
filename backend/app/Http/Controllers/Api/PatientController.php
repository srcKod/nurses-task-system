<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\PatientStoreRequest;
use App\Http\Resources\PatientResource;
use App\Models\Patient;
use App\Traits\ApiResponseTrait;
use App\Traits\PatientTrait;
use Illuminate\Http\Request;

class PatientController extends Controller
{
    use PatientTrait, ApiResponseTrait;

    /**
     * @OA\Get(
     *     path="/api/patients",
     *     tags={"Patient"},
     *     summary="Get Patients",
     *     description="Multiple status values can be provided with comma separated string",
     *     operationId="getPatients",
     *     deprecated=false,
     *     @OA\Response(response=200,description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Patient")
     *      )
     *    ),
     *   @OA\Response(response="404",description="No Patients was found"),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getPatients()
    {
        $patients = PatientResource::collection(Patient::get());
        if($patients->isEmpty()){
            return $this->apiResponse(null, 404,'No Patients was found');
        }
        return $this->apiResponse($patients, 200,'ok');
    }

    /**
     * @OA\Get(
     *     path="/api/patients/{id}",
     *     tags={"Patient"},
     *     summary="Get Patient",
     *     description="Patient id should be provided",
     *     operationId="getPatient",
     *     @OA\Parameter(
     *       name="id",
     *       in="path",
     *       description="Get Patient",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Patient")
     *       )
     *     ),
     *   @OA\Response(response="404",description="A Patient with the specified ID was not found."),
     *   security={{ "apiAuth": {} }}
     * )
     */

    public function getPatient($id)
    {
        $patient = Patient::find($id);
        if(!$patient){
            return $this->apiResponse($patient, 404,'A Patient with the specified ID was not found.');
        }
        return $this->apiResponse(new PatientResource($patient), 200,'ok');
    }

    /**
     * @OA\Post(
     *   path="/api/patients",
     *   tags={"Patient"},
     *   summary="Create Patient",
     *   description="Multiple values can be provided with comma separated string",
     *   operationId="createPatient",
     *   @OA\RequestBody(
     *     description="Patient to create",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Patient")
     *   ),
     *  @OA\Response(response="201",description="Patient created",
     *   @OA\JsonContent(
     *     @OA\Property(
     *      title="data",
     *      property="data",
     *      type="object",
     *      ref="#/components/schemas/Patient"
     *     ),
     *    ),
     *  ),
     *  @OA\Response(response=400,description="Bad Request"),
     *  @OA\Response(response=422,description="Validation exception"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function createPatient(PatientStoreRequest $request)
    {
        $patient = new Patient();
        $this->saveRequestData($request, $patient);
        return $this->apiResponse(new PatientResource($patient), 201,'Patient created');
    }

    /**
     * @OA\Patch(
     *   path="/api/patients",
     *   tags={"Patient"},
     *   summary="Update Patient",
     *   description="Update Patient",
     *   operationId="updatePatient",
     *   @OA\Parameter(ref="#/components/parameters/Patient--id"),
     *   @OA\Response(response="201",description="Patient updated",
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="data",
     *       property="data",
     *       type="object",
     *       ref="#/components/schemas/Patient"
     *       ),
     *     ),
     *   ),
     *   @OA\Response(response="400",description="Bad request."),
     *   @OA\Response(response="404",description="A Patient with the specified ID was not found."),
     *   @OA\RequestBody(
     *     description="Patient to update",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Patient")
     *   ),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function updatePatient(PatientStoreRequest $request)
    {
        $patient = Patient::find($request->id);
        if(!$patient){
            return $this->apiResponse(null, 404,'A Patient with the specified ID was not found.');
        }
        $this->saveRequestData($request, $patient);
        return $this->apiResponse(new PatientResource($patient), 201,'Patient updated');
    }

    /**
     * @OA\Delete(
     *  path="/api/patients/{id}",
     *  summary="Delete Patient",
     *  description="Delete Patient",
     *  operationId="deletePatient",
     *  tags={"Patient"},
     *  @OA\Parameter(ref="#/components/parameters/Patient--id"),
     *  @OA\Response(response=200,description="ok"),
     *  @OA\Response(response=401,description="Unauthorized"),
     *  @OA\Response(response=404,description="Patient not found"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function deletePatient($id)
    {
        $patient = Patient::find($id);
        if(!$patient){
            return $this->apiResponse($patient, 404,'No Patient with specified id was found');
        }
        $patient->delete();
        return $this->apiResponse(null, 200,'ok');
    }
}
